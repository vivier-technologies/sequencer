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

package com.vivier_technologies.admin;

import com.vivier_technologies.utils.ByteArrayUtils;

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
    private final byte[] _instance = new byte[Command.INSTANCE_NAME_LEN];

    public final Command setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }

    @Override
    public final byte[] getInstance() {
        ByteArrayUtils.copy(_buffer, Command.INSTANCE_NAME, _instance, 0, Command.INSTANCE_NAME_LEN);
        return _instance;
    }

    @Override
    public final short getType() {
        return _buffer.getShort(Command.TYPE);
    }
}