package com.vivier_technologies.sequencer.receiver;

import java.io.IOException;

public interface CommandReceiver {

    void open() throws IOException;

    void close();

    void setListener(CommandListener listener);
}
