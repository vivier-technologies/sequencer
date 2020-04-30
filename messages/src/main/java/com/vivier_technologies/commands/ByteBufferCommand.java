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
