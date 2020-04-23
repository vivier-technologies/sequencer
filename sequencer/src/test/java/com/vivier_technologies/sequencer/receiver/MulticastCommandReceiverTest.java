package com.vivier_technologies.sequencer.receiver;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MulticastCommandReceiverTest {

    @Test
    public void testBufferSizeCheck() throws IOException {
        CommandReceiver receiver = new MulticastCommandReceiver(null, null, "127.0.0.1",
                "239.0.0.0", 4000, true, 8192, 65535);
        assertThrows(IllegalArgumentException.class, receiver::open);

    }

}