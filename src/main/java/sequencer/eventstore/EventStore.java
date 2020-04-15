package sequencer.eventstore;

import sequencer.events.Event;

import java.io.IOException;

public interface EventStore {

    void open() throws IOException;

    boolean store(Event event);

    Events retrieve(long start, long end);

}
