package com.vivier_technologies.sequencer.emitter;

import com.vivier_technologies.events.ByteBufferEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventEmitterTest {

    @Test
    public void testSendingEvent() throws IOException {
        EventEmitter ee = new MulticastEventEmitter("127.0.0.1", "230.0.0.0", 4000, true, 8192);
        ByteBufferEvent e = new ByteBufferEvent();
        e.setData(ByteBuffer.wrap("TEST".getBytes()));
        assertTrue(ee.send(e));
    }
}
