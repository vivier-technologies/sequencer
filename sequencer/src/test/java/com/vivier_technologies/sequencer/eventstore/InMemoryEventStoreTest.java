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
import com.vivier_technologies.events.EventHeader;
import com.vivier_technologies.events.Events;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.ConsoleLogger;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryEventStoreTest {

    private Configuration getConfig() {
        Configuration config = mock(Configuration.class);
        when(config.getLong("sequencer.eventstore.initialsize", 1024)).thenReturn(10L);
        when(config.getInt("maxmessagesize")).thenReturn(100);
        return config;
    }

    @Test
    public void testIsEmpty() throws Exception {
        EventStore es = new InMemoryEventStore(new ConsoleLogger(), getConfig());
        es.open();
        assertTrue(es.isEmpty());

        ByteBufferEvent e = new ByteBufferEvent();
        byte[] source = "TESTSRC ".getBytes();
        byte[] eventBody = "MY_TEST_BODY".getBytes();
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(100);
        buffer.putShort(EventHeader.TYPE, Events.START_OF_STREAM);
        buffer.put(EventHeader.SRC, source, 0, source.length);
        buffer.putLong(EventHeader.EVENT_SEQ, es.getNextSequence());
        buffer.position(EventHeader.EVENT_HEADER_LEN);
        buffer.put(eventBody);
        buffer.putInt(EventHeader.EVENT_LEN, buffer.position());
        buffer.flip();
        e.setData(buffer);

        es.store(e);
        assertFalse(es.isEmpty());
        es.close();
    }

    @Test
    public void testGrowth() throws Exception {
        EventStore es = new InMemoryEventStore(new ConsoleLogger(), getConfig());
        ByteBufferEvent e = new ByteBufferEvent();
        byte[] source = "TESTSRC ".getBytes();
        byte[] eventBody = "MY_TEST_BODY".getBytes();
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(1000);
        buffer.putShort(EventHeader.TYPE, Events.START_OF_STREAM);
        buffer.put(EventHeader.SRC, source, 0, source.length);
        buffer.position(EventHeader.EVENT_HEADER_LEN);
        for(int i=0;i<10;i++)
            buffer.put(eventBody);
        buffer.putInt(EventHeader.EVENT_LEN, buffer.position());
        buffer.flip();
        e.setData(buffer);

        es.open();
        for(int i=0;i<20;i++) {
            buffer.putLong(EventHeader.EVENT_SEQ, es.getNextSequence());
            es.store(e);
        }
        es.close();
    }

    @Test
    public void testRetrieve() throws Exception {
        EventStore es = new InMemoryEventStore(new ConsoleLogger(), getConfig());
        es.open();
        assertTrue(es.isEmpty());

        ByteBufferEvent e = new ByteBufferEvent();
        byte[] source = "TESTSRC ".getBytes();
        byte[] eventBody = "MY_TEST_BODY".getBytes();
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(100);
        buffer.putShort(EventHeader.TYPE, Events.START_OF_STREAM);
        buffer.put(EventHeader.SRC, source, 0, source.length);
        buffer.putLong(EventHeader.EVENT_SEQ, es.getNextSequence());
        buffer.position(EventHeader.EVENT_HEADER_LEN);
        buffer.put(eventBody);
        buffer.putInt(EventHeader.EVENT_LEN, buffer.position());
        buffer.flip();
        e.setData(buffer);

        for(int i=0;i<20;i++) {
            buffer.putLong(EventHeader.EVENT_SEQ, es.getNextSequence());
            es.store(e);
        }

        Event retrievedEvent = es.retrieve(0);
        assertEquals(0, retrievedEvent.getHeader().getSequence());
        assertEquals(Events.START_OF_STREAM, retrievedEvent.getHeader().getType());
        assertEquals(new String(source), new String(retrievedEvent.getHeader().getSource()));

        retrievedEvent = es.retrieve(15);
        assertEquals(15, retrievedEvent.getHeader().getSequence());
        assertEquals(Events.START_OF_STREAM, retrievedEvent.getHeader().getType());
        assertEquals(new String(source), new String(retrievedEvent.getHeader().getSource()));

        es.close();
    }

    @Test
    public void testInvalidRetrieve() throws Exception {
        EventStore es = new InMemoryEventStore(new ConsoleLogger(), getConfig());
        assertTrue(es.isEmpty());
        Event e = es.retrieve(5);
        assertNull(e);
    }

}