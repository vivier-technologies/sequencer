package com.vivier_technologies.events;

import java.nio.ByteBuffer;

/**
 * Represents a single sequenced event
 */
public interface Event {

    //TODO consider whether bytebuffer is the right abstraction here
    ByteBuffer getData();
}
