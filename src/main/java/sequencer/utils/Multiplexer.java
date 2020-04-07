package sequencer.utils;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.spi.AbstractSelectableChannel;

public interface Multiplexer {

    void register(AbstractSelectableChannel channel, int ops, MultiplexerListener handler) throws ClosedChannelException;

    void remove(AbstractSelectableChannel channel);
}
