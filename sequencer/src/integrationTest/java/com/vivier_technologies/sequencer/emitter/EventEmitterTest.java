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

package com.vivier_technologies.sequencer.emitter;

import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.MulticastNetworkChannelCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EventEmitterTest {

    @Test
    public void testSendingEvent() throws IOException {
        EventEmitter ee = new MulticastEventEmitter(new ConsoleLogger(), new MulticastNetworkChannelCreator(),
                "127.0.0.1", "230.0.0.0", 4000,
                true, 8192, 512);
        ee.open();
        ByteBufferEvent e = new ByteBufferEvent();
        e.setData(ByteBuffer.wrap("TESTTESTTEST".getBytes()));
        assertDoesNotThrow(() -> ee.send(e));
    }
}
