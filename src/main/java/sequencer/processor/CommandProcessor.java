package sequencer.processor;

import sequencer.commands.Command;
import sequencer.events.Event;

public interface CommandProcessor {

    Event process(Command command);

    String getName();
}
