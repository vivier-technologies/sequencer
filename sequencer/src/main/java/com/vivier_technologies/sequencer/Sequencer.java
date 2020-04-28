package com.vivier_technologies.sequencer;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.common.admin.AdminListener;
import com.vivier_technologies.common.admin.AdminReceiver;
import com.vivier_technologies.common.admin.Status;
import com.vivier_technologies.common.admin.StatusEmitter;
import com.vivier_technologies.common.eventreceiver.EventListener;
import com.vivier_technologies.common.eventreceiver.EventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.events.EventHeader;
import com.vivier_technologies.sequencer.commandreceiver.CommandListener;
import com.vivier_technologies.sequencer.commandreceiver.CommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.utils.ByteArrayUtils;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;

public class Sequencer implements CommandListener, EventListener, AdminListener, Status {

    private static final byte[] _componentName = Logger.generateLoggingKey("SEQUENCER");

    private static final byte[] _source = new byte[EventHeader.SRC_LEN];

    private final CommandReceiver _commandReceiver;
    private final EventReceiver _eventReceiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _eventEmitter;
    private final Multiplexer _mux;
    private final EventReplay _replayer;
    private final StatusEmitter _statusEmitter;
    private final AdminReceiver _adminReceiver;

    private final Logger _logger;

    private boolean _active = false;

    @Inject
    public Sequencer(Logger logger, Configuration configuration, Multiplexer mux, CommandProcessor processor,
                     EventStore eventStore, EventEmitter eventEmitter, CommandReceiver commandReceiver,
                     EventReceiver eventReceiver, EventReplay replayer, StatusEmitter statusEmitter,
                     AdminReceiver adminReceiver) {

        String source = configuration.getString("source");
        ByteArrayUtils.copyAndPadRightWithSpaces(source.getBytes(), _source, 0, _source.length);

        _logger = logger;
        _mux = mux;
        _processor = processor;
        _eventStore = eventStore;
        _eventEmitter = eventEmitter;
        _commandReceiver = commandReceiver;
        _eventReceiver = eventReceiver;
        _replayer = replayer;
        _statusEmitter = statusEmitter;
        _adminReceiver = adminReceiver;

        _commandReceiver.setListener(this);
        _eventReceiver.setListener(this);
    }

    public CommandProcessor getProcessor() {
        return _processor;
    }

    //TODO implement scheduler on top of mux
    //TODO event storage and structures
    //TODO heartbeating
    //TODO event replay!!

    public void start() {
        try {
            _mux.open();
            _eventStore.open();
            _statusEmitter.open();

            /////TEMP//////
            onGoActive();
            ///////////////

            _mux.run();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to start as cannot open dependent modules");
        }
    }

    public void stop() {
        // close receiver first
        _commandReceiver.close();

        _eventReceiver.close();
        _replayer.close();
        _eventEmitter.close();
        _statusEmitter.close();
        _eventStore.close();

        _mux.close();
    }

    @Override
    public void onCommand(Command command) {
        Event e = _processor.process(command);
        // TODO consider what to do when can't store and whether we should listen to event on the network - gives more common codepath..
        _eventStore.store(e);
        try {
            _eventEmitter.send(e);
        } catch (IOException ioException) {
            _logger.error(_componentName, "Unable to send event for command - clients will request replay from sequencer");
        }
    }

    @Override
    public void onEvent(Event event) {
        Event e = _processor.process(event);
        // store here so backup sequencers can be a source of replay and limit impact to primary though repeater
        // will still be preferred source
        // TODO consider what to do when can't store...
        _eventStore.store(e);
        // not sending out because only the command receiver that is active does that
    }

    @Override
    public void onGoActive() {
        try {
            _commandReceiver.open();
            _eventEmitter.open();
            _active = true;
            // TODO send out start of stream if its the start of the stream
            if(_eventStore.isEmpty()) {

            }
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to go active as cannot open command receiver");
        }
    }

    @Override
    public void onGoPassive() {
        try {
            // start listening to events on assumption another sequencer is taking over
            _eventReceiver.open();
            // stop listening to commands
            _commandReceiver.close();
            _eventEmitter.close();
            _active = false;
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to go passive as cannot open event receiver");
        }
    }

    @Override
    public void onShutdown() {
        if(_active) {
            // TODO send end of stream out
        }
        stop();
    }

    @Override
    public boolean isActive() {
        return _active;
    }
}


