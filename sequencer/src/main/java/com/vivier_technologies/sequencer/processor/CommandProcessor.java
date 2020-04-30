package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.Event;

public interface CommandProcessor {

    /**
     * Called when the sequencer hosting the processor is active and the master for receiving commands
     * and publishing events
     * @param command
     * @return
     */
    Event process(Command command);

    /**
     * Called when the sequencer hosting the processor is passive and a slave of events
     * @param event
     * @return
     */
    Event process(Event event);

    String getName();
}
