package com.vivier_technologies.sequencer.eventreceiver;

import com.vivier_technologies.common.mux.MultiplexerListener;

import java.io.IOException;

public class MulticastEventReceiver implements EventReceiver, MultiplexerListener {
    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

    }

    @Override
    public void setListener(EventListener listener) {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onAccept() {

    }

    @Override
    public void onRead() {
        // TODO need to cater to getting an event out of sequence and requesting replay
    }

    @Override
    public void onWrite() {

    }
}
