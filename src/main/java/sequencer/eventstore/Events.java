package sequencer.eventstore;

import sequencer.events.Event;

/**
 * Efficient wrapper of lots of events
 */
public interface Events {

    Event getNextEvent();
}
