package com.vivier_technologies.common.mux;

public interface MultiplexerListener {

    void onConnect();

    void onAccept();

    void onRead();

    void onWrite();

}
