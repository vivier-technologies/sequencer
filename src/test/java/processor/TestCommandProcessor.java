package processor;

import sequencer.commands.Command;

public class TestCommandProcessor implements CommandProcessor {

    @Override
    public boolean process(Command command) {
        return false;
    }

    @Override
    public String getName() {
        return "Test";
    }
}
