package sequencer.eventstore;

import sequencer.events.Event;

import java.io.IOException;

public class TestEventStore implements EventStore {

    @Override
    public boolean store(Event event) {
        return false;
    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public Events retrieve(long start, long end) {
        return null;
    }
}
