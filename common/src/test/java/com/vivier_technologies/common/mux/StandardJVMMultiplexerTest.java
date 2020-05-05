package com.vivier_technologies.common.mux;

import com.vivier_technologies.utils.ConsoleLogger;
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
        Multiplexer mux = new StandardJVMMultiplexer(new ConsoleLogger());
        mux.open();

        AbstractSelectableChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        mux.register(channel, SelectionKey.OP_WRITE, new MultiplexerHandler() {
            @Override
            public void onConnect() {
                fail();
            }

            @Override
            public void onAccept() {
                fail();
            }

            @Override
            public void onRead() {
                fail();
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
                mux.close();
            }
        });
        mux.run();
        assertFalse(mux.isRunning());
    }

}