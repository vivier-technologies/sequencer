package com.vivier_technologies.events;

import java.nio.ByteBuffer;

/**
 * Very basic naive implementation using standard java bytebuffer impl
 *
 * Will exhibit reasonable performance but need another implementation to go faster
 *
 * Methods marked as final for inlining
 *
 * No checking is deliberate - could think about adding checks based on system property later..
 *
 * Intended to be run single threaded so not worrying about padding to avoid false sharing etc
 */
public class ByteBufferEvent implements Event {

    private ByteBuffer _buffer;
    private ByteBufferEventHeader _header = new ByteBufferEventHeader();

    @Override
    public EventHeader getHeader() {
        return _header.setData(_buffer);
    }

    @Override
    public final ByteBuffer getData() {
        return _buffer;
    }

    public final void setData(ByteBuffer data) {
        _buffer = data;
    }
}
