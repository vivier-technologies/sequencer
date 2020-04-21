package com.vivier_technologies.commands;

import java.nio.ByteBuffer;

/**
 * Very basic naive implementation using standard java bytebuffer impl
 *
 * Will exhibit reasonable performance but need another implementation to go faster
 *
 * Methods marked as final for inlining
 */
public class ByteBufferCommand implements Command {

    private ByteBuffer _buffer;
    private ByteBufferCommandHeader _header = new ByteBufferCommandHeader();

    @Override
    public CommandHeader getHeader() {
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
