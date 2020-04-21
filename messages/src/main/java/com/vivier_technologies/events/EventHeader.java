package com.vivier_technologies.events;

/**
 * Represents the header for a single sequenced event
 */
public interface EventHeader {

    int EVENT_LEN = 0;
    int TYPE = 4;
    int SRC = 6;
    int SRC_LEN = 8;
    int EVENT_SEQ = SRC + SRC_LEN;

    int EVENT_HEADER_LEN = EVENT_SEQ + 8;

    int getLength();

    short getType();

    byte[] getSource();

    long getSequence();
}
