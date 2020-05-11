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

package com.vivier_technologies.common.eventreceiver;

import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.events.EventHeader;
import com.vivier_technologies.events.Events;
import com.vivier_technologies.utils.ByteArrayUtils;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.MulticastTestChannelCreator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MulticastEventReceiverTest {

    private byte[] _source = new byte[EventHeader.SRC_LEN];
    private byte[] _eventBody = "THIS_IS_A_TEST".getBytes();

    @Test
    public void testGotEvent() throws IOException {
        ByteArrayUtils.copyAndPadRightWithSpaces("TEST".getBytes(), _source, 0, _source.length);

        MulticastTestChannelCreator creator = new MulticastTestChannelCreator();
        DatagramChannel mockChannel = mock(DatagramChannel.class);
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ByteBuffer buffer = (ByteBuffer)args[0];
            buffer.putShort(EventHeader.TYPE, Events.START_OF_STREAM);
            buffer.put(EventHeader.SRC, _source, 0, _source.length);
            buffer.putLong(EventHeader.EVENT_SEQ, 1L);
            buffer.position(EventHeader.EVENT_HEADER_LEN);
            buffer.put(_eventBody);
            buffer.putInt(EventHeader.EVENT_LEN, buffer.position());
            return null; // void method, so return null
        }).when(mockChannel).receive(any());

        creator.setReceiveChannel(mockChannel);
        MulticastEventReceiver receiver =
                new MulticastEventReceiver(mock(ConsoleLogger.class), mock(Multiplexer.class), creator, null, null,
                0, true, 1, 50);
        EventHandler mockEventHandler = mock(EventHandler.class);
        receiver.setHandler(mockEventHandler);
        receiver.open();
        receiver.onRead();
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(mockEventHandler).onEvent(captor.capture());
        assertEquals(1, captor.getValue().getHeader().getSequence());
    }

}