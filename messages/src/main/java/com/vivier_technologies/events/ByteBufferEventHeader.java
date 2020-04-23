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
public class ByteBufferEventHeader implements EventHeader {
    private ByteBuffer _buffer;
    private byte[] _src = new byte[EventHeader.SRC_LEN];

    public final EventHeader setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }

    @Override
    public final int getLength() {
        return _buffer.getInt(EventHeader.EVENT_LEN);
    }

    @Override
    public final short getType() {
        return _buffer.getShort(EventHeader.TYPE);
    }

    @Override
    public final byte[] getSource() {
        if(_buffer.hasArray())
            // array copy much faster but only when backed by array i.e. on heap/non-direct
            System.arraycopy(_buffer.array(), EventHeader.SRC, _src, 0, EventHeader.SRC_LEN);
        else
            _buffer.get(EventHeader.SRC, _src, 0, EventHeader.SRC_LEN);
        return _src;
    }

    @Override
    public final long getSequence() {
        return _buffer.getInt(EventHeader.EVENT_SEQ);
    }
}
