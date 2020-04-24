package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;

public class NoOpCommandProcessor implements CommandProcessor {
    private static final byte[] _componentName = Logger.generateLoggingKey("NOOPCMDPROC");

    private final Logger _logger;
    private final ByteBufferEvent _event;

    @Inject
    public NoOpCommandProcessor(Logger logger, Configuration configuration) {
        _logger = logger;

        _event = new ByteBufferEvent();
    }

    @Override
    public final Event process(Command command) {
        //TODO Consider factory here
        //TODO handle command retries or assume client will back off?
        //TODO handle command sequence gaps
        //TODO make sure command size won't breach max message size
        _event.setData(command.getData());
        _logger.info(_componentName, "Received command - processing");
        return _event;
    }

    @Override
    public Event process(Event event) {
        return null;
    }

    @Override
    public final String getName() {
        return null;
    }
}
