package com.vivier_technologies.commands;

import java.nio.ByteBuffer;

/**
 * Very basic naive implementation using standard java bytebuffer impl
 *
 * Will exhibit reasonable performance but need another implementation to go faster
 *
 * Methods marked as final for inlining
 */
public class ByteBufferCommandHeader implements CommandHeader {
    private ByteBuffer _buffer;
    private byte[] _src = new byte[CommandHeader.SRC_LEN];

    public final CommandHeader setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
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
