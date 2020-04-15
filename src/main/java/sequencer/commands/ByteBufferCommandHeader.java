package sequencer.commands;

import java.nio.ByteBuffer;

public class ByteBufferCommandHeader implements CommandHeader {
    private ByteBuffer _buffer;

    public ByteBufferCommandHeader setData(ByteBuffer buffer) {
        _buffer = buffer;
        return this;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public short getType() {
        return 0;
    }

    @Override
    public byte[] getSource() {
        return new byte[0];
    }

    @Override
    public int getSequence() {
        return 0;
    }
}
