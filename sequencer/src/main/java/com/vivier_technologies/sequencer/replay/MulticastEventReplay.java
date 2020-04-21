package com.vivier_technologies.sequencer.replay;

import java.io.IOException;

/**
 * Last resort replay - should hit a TCP based repeater of the stream
 */
public class MulticastEventReplay implements EventReplay {
    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

    }
}
