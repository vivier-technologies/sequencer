package com.vivier_technologies.sequencer.emitter;

import com.vivier_technologies.events.Event;

import java.io.IOException;

public interface EventEmitter {

    boolean send(Event event) throws IOException;

    void open() throws IOException;

}
