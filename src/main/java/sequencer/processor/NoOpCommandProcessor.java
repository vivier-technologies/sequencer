package sequencer.processor;

import sequencer.commands.Command;
import sequencer.events.ByteBufferEvent;
import sequencer.events.Event;

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
