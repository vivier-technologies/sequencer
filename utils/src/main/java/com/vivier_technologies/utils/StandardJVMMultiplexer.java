package com.vivier_technologies.utils;

import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Uses standard VM selector implementation which generates quite a bit of garbage..
 */
public class StandardJVMMultiplexer implements Multiplexer, Scheduler {
    private Selector _selector;
    private Logger _logger;
    private boolean _run = true;


    @Inject
    public StandardJVMMultiplexer(Configuration config, Logger logger) {
        _logger = logger;
    }

    @Override
    public boolean schedule(SchedulerListener listener) {
        return false;
    }

    public void open() throws IOException {
        _selector = SelectorProvider.provider().openSelector();
    }

    public void run() throws IOException {

        while(_run) {
            // TODO work out the timeout logic here
            long now = System.currentTimeMillis();
            if(_selector.selectNow() > 0) {
                Iterator<SelectionKey> iterator = _selector.selectedKeys().iterator();
                iterator.forEachRemaining(key -> {
                    if(key.isAcceptable()) {
                        ((MultiplexerListener)key.attachment()).onAccept();

                    } else if (key.isConnectable()) {
                        ((MultiplexerListener)key.attachment()).onConnect();

                    } else if (key.isReadable()) {
                        ((MultiplexerListener)key.attachment()).onRead();

                    } else if (key.isWritable()) {
                        ((MultiplexerListener)key.attachment()).onWrite();
                    }

                    iterator.remove();
                });

            }
        }
        _selector.close();
    }

    @Override
    public boolean isRunning() {
        return _run;
    }

    public void shutdown() {
        _run = false;
    }

    @Override
    public void register(AbstractSelectableChannel channel, int ops, MultiplexerListener handler)
            throws ClosedChannelException {
        channel.register(_selector, ops, handler);
    }

    @Override
    public void remove(AbstractSelectableChannel channel) {
        SelectionKey key = channel.keyFor(_selector);
        key.cancel();
    }
}
