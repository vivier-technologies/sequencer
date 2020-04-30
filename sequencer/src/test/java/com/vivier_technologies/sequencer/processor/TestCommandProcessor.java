package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.Event;

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

    @Override
    public Event process(Event event) {
        return null;
    }
}
