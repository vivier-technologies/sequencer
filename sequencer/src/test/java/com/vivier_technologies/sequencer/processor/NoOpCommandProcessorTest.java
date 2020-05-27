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

package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.eventstore.InMemoryEventStore;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NoOpCommandProcessorTest {

    private Configuration getConfig() {
        Configuration config = mock(Configuration.class);
        when(config.getLong("sequencer.eventstore.initialsize", 1024)).thenReturn(10L);
        when(config.getInt("maxmessagesize")).thenReturn(100);
        return config;
    }

    @Test
    public void testProcessingCommand() {
        Logger logger = new ConsoleLogger();
        Configuration config = getConfig();
        EventStore es = new InMemoryEventStore(logger, config);
        assertTrue(es.isEmpty());
        CommandProcessor cp = new NoOpCommandProcessor(logger, config, es);
        ByteBufferCommand command = new ByteBufferCommand();
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(100);
        byte[] body = "TEST_THIS_APPEARS_IN_BODY".getBytes();
        byte[] eventBody = new byte[body.length];
        String source = "SOURCE01";
        buffer.put(Command.CMD_BODY_START, body);
        command.setData(buffer);
        command.getHeader().setHeader(Command.CMD_BODY_START + body.length, (short)1, source.getBytes(), 1);
        buffer.position(0).limit(Command.CMD_BODY_START + body.length);

        Event event = cp.process(command);
        assertEquals(0, event.getHeader().getSequence());
        event.getData().get(Event.EVENT_BODY_START, eventBody);
        assertEquals(new String(body), new String(eventBody));
        assertEquals(Event.EVENT_BODY_START + body.length, event.getHeader().getLength());
        assertEquals(source, new String(event.getHeader().getSource()));
    }

    @Test
    public void testProcessingEvent() {
        Event event = new ByteBufferEvent();
        Logger logger = new ConsoleLogger();
        Configuration config = getConfig();
        EventStore es = new InMemoryEventStore(logger, config);
        CommandProcessor cp = new NoOpCommandProcessor(logger, config, es);
        assertEquals(event, cp.process(event));
    }

}