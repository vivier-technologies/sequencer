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

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.events.EventHeader;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.nio.ByteBuffer;

public class NoOpCommandProcessor implements CommandProcessor {
    private static final byte[] _loggingKey = Logger.generateLoggingKey("NOOPCMDPROC");

    private final Logger _logger;
    private final ByteBufferEvent _event;
    private final ByteBuffer _buffer;

    private final EventStore _eventStore;

    @Inject
    public NoOpCommandProcessor(Logger logger, Configuration configuration, EventStore eventStore) {
        _logger = logger;

        // TODO not going direct here because improves copy semantics into store but should be really to enable faster IO
        _buffer = ByteBufferFactory.nativeAllocate(configuration.getInt("maxmessagesize"));

        _event = new ByteBufferEvent();
        _event.setData(_buffer);

        _eventStore = eventStore;
    }

    @Override
    public final Event process(Command command) {
        _logger.info(_loggingKey, "Received command - processing");
        long sequenceNumber = _eventStore.getNextSequence();
        ByteBuffer commandBuffer = command.getData();
        _buffer.clear();
        // copy command body for now given this is a no-op processor - limit will already be set correctly
        _buffer.position(Event.EVENT_BODY_START).put(commandBuffer.position(Command.CMD_BODY_START));
        // TODO may want a setter on the eventheader rather this little lot
        _buffer.putInt(EventHeader.EVENT_LEN, (commandBuffer.limit() - Command.CMD_BODY_START) + EventHeader.EVENT_HEADER_LEN);
        // not that efficient - as copy into the source byte[] then copy back into buffer but this is the no-op case will do for now
        _buffer.put(EventHeader.SRC, command.getHeader().getSource());
        // just copy the type over from the command for now given this is a no-op processor -  reality is something more
        // interesting will be wanting to be done
        _buffer.putShort(EventHeader.TYPE, command.getHeader().getType());
        _buffer.flip();
        return _event;
    }

    @Override
    public Event process(Event event) {
        // no-op processor so just return the event - normally state would be updated here
        return event;
    }

    @Override
    public final String getName() {
        return this.getClass().getSimpleName();
    }
}
