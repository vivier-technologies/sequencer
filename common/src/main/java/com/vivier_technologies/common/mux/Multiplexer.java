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

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.spi.AbstractSelectableChannel;

public interface Multiplexer {

    void open() throws IOException;

    void run() throws IOException;

    void register(AbstractSelectableChannel channel, int ops, MultiplexerHandler handler) throws ClosedChannelException;

    void remove(AbstractSelectableChannel channel);

    void close();

    boolean isRunning();
}
