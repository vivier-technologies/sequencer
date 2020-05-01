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
public class ByteBufferCommandHeader implements CommandHeader {
    private ByteBuffer _buffer;
    private byte[] _src = new byte[CommandHeader.SRC_LEN];

    public final CommandHeader setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }

    @Override
    public void setHeader(int length, short type, byte[] src, int sequence) {
        _buffer.putInt(CommandHeader.CMD_LEN, length);
        _buffer.putShort(CommandHeader.TYPE, type);
        _buffer.put(CommandHeader.SRC, src);
        _buffer.putInt(CommandHeader.CMD_SEQ, sequence);
    }

    @Override
    public final int getLength() {
        return _buffer.getInt(CommandHeader.CMD_LEN);
    }

    @Override
    public final short getType() {
        return _buffer.getShort(CommandHeader.TYPE);
    }

    @Override
    public final byte[] getSource() {
        if(_buffer.hasArray())
            // array copy much faster but only when backed by array i.e. on heap/non-direct
            System.arraycopy(_buffer.array(), CommandHeader.SRC, _src, 0, CommandHeader.SRC_LEN);
        else
            _buffer.get(CommandHeader.SRC, _src, 0, CommandHeader.SRC_LEN);
        return _src;
    }

    @Override
    public final int getSequence() {
        return _buffer.getInt(CommandHeader.CMD_SEQ);
    }
}
