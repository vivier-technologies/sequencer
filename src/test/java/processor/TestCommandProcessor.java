package processor;

import com.google.inject.Inject;
import sequencer.commands.Command;

public class TestCommandProcessor implements CommandProcessor {

    @Inject
    public TestCommandProcessor() {

    }

    @Override
    public boolean process(Command command) {
        return false;
    }

    @Override
    public String getName() {
        return "Test";
    }
}
