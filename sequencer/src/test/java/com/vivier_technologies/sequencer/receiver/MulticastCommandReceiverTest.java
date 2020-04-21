package com.vivier_technologies.sequencer.receiver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MulticastCommandReceiverTest {

    @Test
    public void testBufferSizeCheck() {
        assertThrows(IllegalArgumentException.class,
                () -> new MulticastCommandReceiver(null, null, null, "127.0.0.1",
                        "239.0.0.0", 4000, true, 8192));
    }

}