package sequencer.processor;

import sequencer.commands.Command;
import sequencer.events.Event;

public class NoOpCommandProcessor implements CommandProcessor {

    @Override
    public Event process(Command command) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
