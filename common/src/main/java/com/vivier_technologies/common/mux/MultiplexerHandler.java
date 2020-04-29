package com.vivier_technologies.common.mux;

public interface MultiplexerHandler {

    void onConnect();

    void onAccept();

    void onRead();

    void onWrite();

}
