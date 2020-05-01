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

import com.vivier_technologies.utils.ByteBufferFactory;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ByteBufferEventTest {
    
    @Test
    public void testSetAndReadDirect() {
        setAndCheck(ByteBufferFactory.nativeAllocateDirect(50));
    }

    @Test
    public void testSetAndReadNonDirect() {
        setAndCheck(ByteBufferFactory.nativeAllocate(50));
    }

    public void setAndCheck(ByteBuffer buffer) {
        ByteBufferEvent event = new ByteBufferEvent();
        assertNull(event.getData());
        buffer.putShort(EventHeader.TYPE, (short)1);
        buffer.put(EventHeader.SRC, "TESTTEST".getBytes(), 0, EventHeader.SRC_LEN);

        byte[] body = "TESTTHISWORKS".getBytes();
        buffer.put(event.EVENT_BODY_START, body, 0, body.length);
        buffer.putInt(EventHeader.EVENT_SEQ, 1);
        buffer.putInt(EventHeader.EVENT_LEN, event.EVENT_BODY_START + body.length);

        event.setData(buffer);
        assertEquals(event.EVENT_BODY_START + body.length, event.getHeader().getLength());
        assertEquals(1, event.getHeader().getType());
        assertEquals(1, event.getHeader().getSequence());
        assertEquals("TESTTEST", new String(event.getHeader().getSource()));
        byte[] readBody = new byte[100];
        event.getData().get(event.EVENT_BODY_START, readBody, 0,
                event.getHeader().getLength() - event.EVENT_BODY_START);
        assertEquals(new String(body), new String(readBody, 0,
                event.getHeader().getLength() - event.EVENT_BODY_START));
    }
}