package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.Event;

/**
 * Efficient wrapper of lots of events
 */
public interface Events {

    Event getNextEvent();
}
