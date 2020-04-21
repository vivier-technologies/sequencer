package com.vivier_technologies.sequencer.replay;

import java.io.IOException;

public interface EventReplay {

    void open() throws IOException;

    void close();
}
