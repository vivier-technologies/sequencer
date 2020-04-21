package com.vivier_technologies.events;

import java.nio.ByteBuffer;

/**
 * Very basic naive implementation using standard java bytebuffer impl
 *
 * Will exhibit reasonable performance but need another implementation to go faster
 *
 */
public class ByteBufferEvent implements Event {

    private ByteBuffer _buffer;

    @Override
    public ByteBuffer getData() {
        return _buffer;
    }

    public void setData(ByteBuffer data) {
        _buffer = data;
    }
}
