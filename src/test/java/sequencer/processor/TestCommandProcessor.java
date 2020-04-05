package sequencer.processor;

import sequencer.commands.Command;
import sequencer.events.Event;

import javax.inject.Inject;

public class TestCommandProcessor implements CommandProcessor {

    @Inject
    public TestCommandProcessor() {

    }

    @Override
    public Event process(Command command) {
        return null;
    }

    @Override
    public String getName() {
        return "Test";
    }
}
