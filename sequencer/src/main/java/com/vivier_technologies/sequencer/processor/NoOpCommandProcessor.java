package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;

public class NoOpCommandProcessor implements CommandProcessor {

    private ByteBufferEvent _event;

    @Override
    public Event process(Command command) {
        // TODO Consider factory here
        _event.setData(command.getData());
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
