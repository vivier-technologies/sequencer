package com.vivier_technologies.utils;

public interface MultiplexerListener {

    void onConnect();

    void onAccept();

    void onRead();

    void onWrite();

}
