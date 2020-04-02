package sequencer.eventstore;

import sequencer.events.Event;

public interface EventStore {

    boolean store(Event event);

    void retrieve();

}
