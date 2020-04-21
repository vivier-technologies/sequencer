package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.Event;

import java.io.IOException;

public interface EventStore {

    void open() throws IOException;

    boolean store(Event event);

    Events retrieve(long start, long end);

    void close();

}
