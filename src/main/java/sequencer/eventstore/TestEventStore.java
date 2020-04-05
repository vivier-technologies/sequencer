package sequencer.eventstore;

import sequencer.events.Event;

public class TestEventStore implements EventStore {

    @Override
    public boolean store(Event event) {
        return false;
    }

    @Override
    public Events retrieve(long start, long end) {
        return null;
    }
}
