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

package com.vivier_technologies.commands;

import java.nio.ByteBuffer;

/**
 * Very basic naive implementation using standard java bytebuffer impl
 *
 * Will exhibit reasonable performance but need another implementation to go faster
 *
 * Methods marked as final for inlining
 *
 * No checking is deliberate - could think about adding checks based on system property later..
 *
 * Intended to be run single threaded so not worrying about padding to avoid false sharing etc
 */
public class ByteBufferCommand implements Command {

    private ByteBuffer _buffer;
    private ByteBufferCommandHeader _header = new ByteBufferCommandHeader();

    public ByteBufferCommand() {
    }

    // convenience only
    public ByteBufferCommand(ByteBuffer buffer) {
        _buffer = buffer;
    }

    @Override
    public final CommandHeader getHeader() {
        return _header.setData(_buffer);
    }

    @Override
    public final ByteBuffer getData() {
        return _buffer;
    }

    public final Command setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }
}
