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

package com.vivier_technologies.common.mux;

import com.vivier_technologies.utils.ConsoleLogger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelectableChannel;

import static org.junit.jupiter.api.Assertions.*;

class StandardJVMMultiplexerTest {

    @Test
    public void testRegisterAndRemove() throws IOException {
        Multiplexer mux = new StandardJVMMultiplexer(new ConsoleLogger());
        mux.open();

        AbstractSelectableChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        mux.register(channel, SelectionKey.OP_WRITE, new MultiplexerHandler() {
            @Override
            public void onConnect() {
                fail();
            }

            @Override
            public void onAccept() {
                fail();
            }

            @Override
            public void onRead() {
                fail();
            }

            @Override
            public void onWrite() {
                assertTrue(mux.isRunning());
                try {
                    mux.register(channel, SelectionKey.OP_READ, this);
                } catch (ClosedChannelException e) {
                    fail();
                }
                mux.remove(channel);
                mux.close();
            }
        });
        mux.run();
        assertFalse(mux.isRunning());
    }

}