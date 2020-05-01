/*
 * Copyright 2020  vivier technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
