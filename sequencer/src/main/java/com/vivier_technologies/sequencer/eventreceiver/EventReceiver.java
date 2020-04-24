package com.vivier_technologies.sequencer.eventreceiver;

import java.io.IOException;

public interface EventReceiver {

    void open() throws IOException;

    void close();

    void setListener(EventListener listener);
}
