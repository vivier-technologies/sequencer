package sequencer.processor;

import sequencer.commands.Command;

public class NoOpCommandProcessor implements CommandProcessor {
    @Override
    public boolean process(Command command) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }
}
