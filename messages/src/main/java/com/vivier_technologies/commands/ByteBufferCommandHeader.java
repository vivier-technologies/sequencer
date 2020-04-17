package com.vivier_technologies.commands;

import java.nio.ByteBuffer;

public class ByteBufferCommandHeader implements CommandHeader {
    private ByteBuffer _buffer;
    private byte[] _src = new byte[CommandHeader.SRC_LEN];

    public ByteBufferCommandHeader setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }

    @Override
    public int getLength() {
        return _buffer.getInt(CommandHeader.CMD_LEN);
    }

    @Override
    public short getType() {
        return _buffer.getShort(CommandHeader.TYPE);
    }

    @Override
    public byte[] getSource() {
        if(_buffer.hasArray())
            // array copy much faster but only when backed by array i.e. non direct
            System.arraycopy(_buffer.array(), CommandHeader.SRC, _src, 0, CommandHeader.SRC_LEN);
        else
            _buffer.get(CommandHeader.SRC, _src, 0, CommandHeader.SRC_LEN);
        return _src;
    }

    @Override
    public int getSequence() {
        return _buffer.getInt(CommandHeader.CMD_SEQ);
    }
}
