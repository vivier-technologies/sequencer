package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.Event;

import java.io.IOException;

public class InMemoryEventStore implements EventStore {
    @Override
    public void open() throws IOException {

    }

    @Override
    public boolean store(Event event) {
        return false;
    }

    @Override
    public Events retrieve(long start, long end) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
