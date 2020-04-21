package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;

public class NoOpCommandProcessor implements CommandProcessor {

    private ByteBufferEvent _event;

    @Override
    public final Event process(Command command) {
        //TODO Consider factory here
        //TODO handle command retries or assume client will back off?
        //TODO handle command sequence gaps
        _event.setData(command.getData());
        return null;
    }

    @Override
    public final String getName() {
        return null;
    }
}
