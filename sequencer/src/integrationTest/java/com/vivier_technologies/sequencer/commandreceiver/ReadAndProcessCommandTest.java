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

package com.vivier_technologies.sequencer.commandreceiver;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.StandardJVMMultiplexer;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.MulticastNetworkChannelCreator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ReadAndProcessCommandTest {

    @Test
    public void testOnRead() throws Exception {
        Multiplexer mux = new StandardJVMMultiplexer(new ConsoleLogger());
        mux.open();
        MulticastCommandReceiver receiver = new MulticastCommandReceiver(null, mux, new MulticastNetworkChannelCreator(),
                "127.0.0.1", "239.0.0.0", 4000, true, 8192, 1472);
        CommandHandler listener = Mockito.mock(CommandHandler.class);
        receiver.setHandler(listener);
        receiver.open();
        receiver.onRead();
        ArgumentCaptor<ByteBufferCommand> argumentCaptor = ArgumentCaptor.forClass(ByteBufferCommand.class);
        Mockito.verify(listener).onCommand(argumentCaptor.capture());
    }
}
