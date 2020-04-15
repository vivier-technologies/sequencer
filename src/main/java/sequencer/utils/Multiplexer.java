package sequencer.utils;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.spi.AbstractSelectableChannel;

public interface Multiplexer {

    void open() throws IOException;

    void run() throws IOException;

    void register(AbstractSelectableChannel channel, int ops, MultiplexerListener handler) throws ClosedChannelException;

    void remove(AbstractSelectableChannel channel);

    void shutdown();

    boolean isRunning();
}
