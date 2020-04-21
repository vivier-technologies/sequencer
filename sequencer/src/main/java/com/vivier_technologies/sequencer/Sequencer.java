package com.vivier_technologies.sequencer;

import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.receiver.CommandReceiver;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.Multiplexer;

import javax.inject.Inject;
import java.io.IOException;

public class Sequencer {

    private static final byte[] _componentName = Logger.generateLoggingKey("SEQUENCER");

    private final CommandReceiver _receiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _emitter;
    private final Multiplexer _mux;
    private final EventReplay _replayer;
    private final Logger _logger;

    @Inject
    public Sequencer(Logger logger, Multiplexer mux, CommandProcessor processor,
                     EventStore eventStore, EventEmitter emitter, CommandReceiver receiver, EventReplay replayer) {

        _logger = logger;
        _mux = mux;
        _processor = processor;
        _eventStore = eventStore;
        _emitter = emitter;
        _receiver = receiver;
        _replayer = replayer;
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
            _emitter.open();
            _receiver.open();
            _eventStore.open();

            _mux.run();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to start as cannot open dependent modules");
        }
    }

    public void stop() {
        // close receiver first
        _receiver.close();
        _emitter.close();
        _eventStore.close();
        _mux.close();
    }
}


