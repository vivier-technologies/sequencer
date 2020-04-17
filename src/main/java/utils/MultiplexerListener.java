package utils;

public interface MultiplexerListener {

    void onConnect();

    void onAccept();

    void onRead();

    void onWrite();

    void onShutdown();

}
