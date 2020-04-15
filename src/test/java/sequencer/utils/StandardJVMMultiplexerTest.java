package sequencer.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelectableChannel;

import static org.junit.jupiter.api.Assertions.*;

class StandardJVMMultiplexerTest {

    @Test
    public void testRegisterAndRemove() throws IOException {
        Multiplexer mux = new StandardJVMMultiplexer(null, new ConsoleLogger());
        mux.open();
        // TODO Mock
        AbstractSelectableChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        mux.register(channel, SelectionKey.OP_WRITE, new MultiplexerListener() {
            @Override
            public void onConnect() {

            }

            @Override
            public void onAccept() {

            }

            @Override
            public void onRead() {

            }

            @Override
            public void onShutdown() {
                assertTrue(true);
            }

            @Override
            public void onWrite() {
                assertTrue(mux.isRunning());
                try {
                    mux.register(channel, SelectionKey.OP_READ, this);
                } catch (ClosedChannelException e) {
                    fail();
                }
                mux.remove(channel);
                mux.shutdown();
            }
        });
        mux.run();
        assertFalse(mux.isRunning());
    }

}