package com.vivier_technologies.admin;

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
        if(_buffer.hasArray())
            // array copy much faster but only when backed by array i.e. on heap/non-direct
            System.arraycopy(_buffer.array(), Command.INSTANCE_NAME, _instance, 0, Command.INSTANCE_NAME_LEN);
        else
            _buffer.get(Command.INSTANCE_NAME, _instance, 0, Command.INSTANCE_NAME_LEN);
        return _instance;
    }

    @Override
    public final short getType() {
        return _buffer.getShort(Command.TYPE);
    }
}