package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.Event;

public interface CommandProcessor {

    Event process(Command command);

    String getName();
}
