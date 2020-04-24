package com.vivier_technologies.sequencer.eventreceiver;

import com.vivier_technologies.events.Event;

public interface EventListener {
    void onEvent(Event event);
}
