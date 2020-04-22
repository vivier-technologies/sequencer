package com.vivier_technologies.events;

import java.nio.ByteBuffer;

/**
 * Represents a single sequenced event
 */
public interface Event {

    int EVENT_BODY_START = EventHeader.EVENT_HEADER_LEN;

    EventHeader getHeader();

    //TODO consider whether bytebuffer is the right abstraction here
    ByteBuffer getData();
}
