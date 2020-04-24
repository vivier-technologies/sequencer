package com.vivier_technologies.sequencer.commandreceiver;

import java.io.IOException;

public interface CommandReceiver {

    void open() throws IOException;

    void close();

    void setListener(CommandListener listener);
}
