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

package com.vivier_technologies.common.admin;

import com.vivier_technologies.admin.Command;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.MulticastTestChannelCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static org.mockito.Mockito.*;

public class MulticastAdminReceiverTest {

    @Test
    public void testGoActive() throws IOException {
        MulticastTestChannelCreator creator = new MulticastTestChannelCreator();
        DatagramChannel mockChannel = mock(DatagramChannel.class);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ByteBuffer buffer = (ByteBuffer)args[0];
            buffer.putShort(Command.Type.GO_ACTIVE);
            buffer.put(Command.validateInstanceName("TEST"));
            return null; // void method, so return null
        }).when(mockChannel).receive(any());

        creator.setReceiveChannel(mockChannel);
        MulticastAdminReceiver receiver = new MulticastAdminReceiver(mock(ConsoleLogger.class),
                mock(Multiplexer.class), creator, "TEST", null, null,
                0, true, 1, 50);
        AdminHandler mockAdminHandler = mock(AdminHandler.class);
        receiver.setHandler(mockAdminHandler);
        receiver.open();
        receiver.onRead();
        verify(mockAdminHandler).onGoActive();
    }

    @Test
    public void testGoPassive() throws IOException {
        MulticastTestChannelCreator creator = new MulticastTestChannelCreator();
        DatagramChannel mockChannel = mock(DatagramChannel.class);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ByteBuffer buffer = (ByteBuffer)args[0];
            buffer.putShort(Command.Type.GO_PASSIVE);
            buffer.put(Command.validateInstanceName("TEST"));
            return null; // void method, so return null
        }).when(mockChannel).receive(any());

        creator.setReceiveChannel(mockChannel);
        MulticastAdminReceiver receiver = new MulticastAdminReceiver(mock(ConsoleLogger.class),
                mock(Multiplexer.class), creator, "TEST", null, null,
                0, true, 1, 50);
        AdminHandler mockAdminHandler = mock(AdminHandler.class);
        receiver.setHandler(mockAdminHandler);
        receiver.open();
        receiver.onRead();
        verify(mockAdminHandler).onGoPassive();
    }

    @Test
    public void testShutdown() throws IOException {
        MulticastTestChannelCreator creator = new MulticastTestChannelCreator();
        DatagramChannel mockChannel = mock(DatagramChannel.class);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ByteBuffer buffer = (ByteBuffer)args[0];
            buffer.putShort(Command.Type.SHUTDOWN);
            buffer.put(Command.validateInstanceName("TEST"));
            return null; // void method, so return null
        }).when(mockChannel).receive(any());

        creator.setReceiveChannel(mockChannel);
        MulticastAdminReceiver receiver = new MulticastAdminReceiver(mock(ConsoleLogger.class),
                mock(Multiplexer.class), creator, "TEST", null, null,
                0, true, 1, 50);
        AdminHandler mockAdminHandler = mock(AdminHandler.class);
        receiver.setHandler(mockAdminHandler);
        receiver.open();
        receiver.onRead();
        verify(mockAdminHandler).onShutdown();
    }

    @Test
    public void testUpdateStatus() throws IOException {
        MulticastTestChannelCreator creator = new MulticastTestChannelCreator();
        DatagramChannel mockChannel = mock(DatagramChannel.class);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ByteBuffer buffer = (ByteBuffer)args[0];
            buffer.putShort(Command.Type.STATUS);
            buffer.put(Command.validateInstanceName("TEST"));
            return null; // void method, so return null
        }).when(mockChannel).receive(any());

        creator.setReceiveChannel(mockChannel);
        MulticastAdminReceiver receiver = new MulticastAdminReceiver(mock(ConsoleLogger.class),
                mock(Multiplexer.class), creator, "TEST", null, null,
                0, true, 1, 50);
        AdminHandler mockAdminHandler = mock(AdminHandler.class);
        receiver.setHandler(mockAdminHandler);
        receiver.open();
        receiver.onRead();
        verify(mockAdminHandler).onStatusRequest();
    }

}