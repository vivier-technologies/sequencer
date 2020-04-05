package sequencer.processor;

import sequencer.commands.Command;

public interface CommandProcessor {

    boolean process(Command command);

    String getName();
}
