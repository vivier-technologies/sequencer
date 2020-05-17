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

package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import it.unimi.dsi.fastutil.BigArrays;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Simple implementation using byte array backing - probably want to move off heap ideally so that buffers can be native
 * and the subsequent IO send is faster but one for later..
 */
public class InMemoryEventStore implements EventStore {

    private final Logger _logger;

    private long[][] _eventLookup = {};
    private byte[][] _store = {};
    private long _nextSequence = 0;
    private long _nextWriteLocation = 0;

    private final byte[] _transferArray;
    private final ByteBuffer _transferBuffer;

    private final ByteBufferEvent _event = new ByteBufferEvent();

    @Inject
    public InMemoryEventStore(Logger logger, Configuration configuration) {
        _logger = logger;

        _transferBuffer = ByteBufferFactory.nativeAllocate(configuration.getInt("maxmessagesize"));
        _transferArray = _transferBuffer.array();
        _event.setData(_transferBuffer);

        _eventLookup = BigArrays.forceCapacity(_eventLookup,
                configuration.getLong("sequencer.eventstore.initialsize", 1024), 0L);
        // initial set just to for clarity - clearly array would be initialised to 0
        BigArrays.set(_eventLookup, _nextSequence, 0);

        _store = BigArrays.forceCapacity(_store,
                configuration.getLong("sequencer.eventstore.initialsize", 1024) *
                        configuration.getInt("maxmessagesize"), 0L);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void open() throws IOException {

    }

    @Override
    public boolean store(Event event) {
        ByteBuffer buffer = event.getData();
        int pos = buffer.position();
        int remaining = buffer.remaining();
        byte[] data;
        //TODO check this externally
        // Grow checks the size internally so to avoiding doing it multiple times not doing it here.. grow also adds 50%
        _eventLookup = BigArrays.grow(_eventLookup, BigArrays.length(_eventLookup) + 1);
        _store = BigArrays.grow(_store, BigArrays.length(_store) + remaining);

        if (buffer.hasArray()) {
            data = buffer.array();
            BigArrays.copyToBig(data, pos, _store, _nextWriteLocation, remaining);
        } else {
            // Bit ugly - adding for completeness in case an author of the processor uses a direct buffer
            // ideally wouldn't do this copy (and still use direct buffer but that will require some more magic..)
            buffer.get(pos, _transferArray, 0, remaining);
            BigArrays.copyToBig(_transferArray, 0, _store, _nextWriteLocation, remaining);
        }
        _nextSequence++;
        _nextWriteLocation += remaining;
        BigArrays.set(_eventLookup, _nextSequence, _nextWriteLocation);
        return true;
    }

    /**
     * Retrieve the given event data for the sequence
     *
     * @param sequence retrieve the event corresponding to this sequence
     * @return the event or null if an invalid sequence was passed in
     */
    @Override
    public Event retrieve(long sequence) {
        if (sequence >= _nextSequence) {
            return null;
        } else {
            long index = BigArrays.get(_eventLookup, sequence);
            long nextIndex = BigArrays.get(_eventLookup, sequence+1);
            int length = (int)(nextIndex-index);
            BigArrays.copyFromBig(_store, index, _transferArray, 0, length);
            _transferBuffer.position(0).limit(length);
            return _event;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public long getNextSequence() {
        return _nextSequence;
    }

    @Override
    public boolean isEmpty() {
        return _nextSequence == 0;
    }
}
