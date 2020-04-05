package sequencer.eventstore;

import sequencer.events.Event;

public interface EventStore {

    boolean store(Event event);

    Events retrieve(long start, long end);

}
