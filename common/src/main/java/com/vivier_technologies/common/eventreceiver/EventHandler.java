package com.vivier_technologies.common.eventreceiver;

import com.vivier_technologies.events.Event;

public interface EventHandler {

    void onEvent(Event event);

}
