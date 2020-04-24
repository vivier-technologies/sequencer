package com.vivier_technologies.sequencer;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.common.admin.AdminListener;
import com.vivier_technologies.common.eventreceiver.EventListener;
import com.vivier_technologies.common.eventreceiver.EventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.sequencer.commandreceiver.CommandListener;
import com.vivier_technologies.sequencer.commandreceiver.CommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.utils.Logger;

import javax.inject.Inject;
import java.io.IOException;

public class Sequencer implements CommandListener, EventListener, AdminListener {

    private static final byte[] _componentName = Logger.generateLoggingKey("SEQUENCER");

    private final CommandReceiver _commandReceiver;
    private final EventReceiver _eventReceiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _emitter;
    private final Multiplexer _mux;
    private final EventReplay _replayer;
    private final Logger _logger;

    private boolean _active = false;

    @Inject
    public Sequencer(Logger logger, Multiplexer mux, CommandProcessor processor,
                     EventStore eventStore, EventEmitter emitter, CommandReceiver commandReceiver,
                     EventReceiver eventReceiver, EventReplay replayer) {

        _logger = logger;
        _mux = mux;
        _processor = processor;
        _eventStore = eventStore;
        _emitter = emitter;
        _commandReceiver = commandReceiver;
        _eventReceiver = eventReceiver;
        _replayer = replayer;

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

            _mux.run();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to start as cannot open dependent modules");
        }
    }

    public void stop() {
        // close receiver first
        _commandReceiver.close();
        _emitter.close();
        _eventStore.close();
        _mux.close();
    }

    @Override
    public void onCommand(Command command) {
        Event e = _processor.process(command);
        // TODO consider what to do when can't store and whether we should listen to event on the network
        _eventStore.store(e);
        try {
            _emitter.send(e);
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
            _active = true;
            // send out start of stream if its the start of the stream
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to go active as cannot open command receiver");
        }
    }

    @Override
    public void onGoPassive() {
        try {
            _eventReceiver.open();
            // stop listening to commands
            _commandReceiver.close();
            _active = false;
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to go passive as cannot open event receiver");
        }
    }

    @Override
    public void onShutdown() {
        if(_active) {
            // send end of stream out
        }
        stop();
    }
}


