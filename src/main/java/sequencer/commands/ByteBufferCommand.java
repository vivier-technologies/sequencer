package sequencer.commands;

import java.nio.ByteBuffer;

public class ByteBufferCommand implements Command {

    private ByteBuffer _buffer;
    private ByteBufferCommandHeader _header = new ByteBufferCommandHeader();

    @Override
    public CommandHeader getHeader() {
        return _header.setData(_buffer);
    }

    @Override
    public ByteBuffer getData() {
        return _buffer;
    }

    public ByteBufferCommand setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }
}
