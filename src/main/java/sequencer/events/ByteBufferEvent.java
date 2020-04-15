package sequencer.events;

import java.nio.ByteBuffer;

public class ByteBufferEvent implements Event {

    private ByteBuffer _buffer;

    @Override
    public ByteBuffer getData() {
        return _buffer;
    }

    public void setData(ByteBuffer data) {
        _buffer = data;
    }
}
