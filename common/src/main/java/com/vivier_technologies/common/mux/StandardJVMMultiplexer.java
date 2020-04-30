package com.vivier_technologies.common.mux;

import com.vivier_technologies.common.scheduling.Scheduler;
import com.vivier_technologies.common.scheduling.SchedulerListener;
import com.vivier_technologies.utils.Logger;

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
 *
 * Needs rework around selection keys to avoid this garbage down the track
 */
public class StandardJVMMultiplexer implements Multiplexer, Scheduler {
    private Selector _selector;
    private Logger _logger;
    private boolean _run = true;


    @Inject
    public StandardJVMMultiplexer(Logger logger) {
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
                        ((MultiplexerHandler)key.attachment()).onAccept();

                    } else if (key.isConnectable()) {
                        ((MultiplexerHandler)key.attachment()).onConnect();

                    } else if (key.isReadable()) {
                        ((MultiplexerHandler)key.attachment()).onRead();

                    } else if (key.isWritable()) {
                        ((MultiplexerHandler)key.attachment()).onWrite();
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

    public void close() {
        _run = false;
    }

    @Override
    public void register(AbstractSelectableChannel channel, int ops, MultiplexerHandler handler)
            throws ClosedChannelException {
        channel.register(_selector, ops, handler);
    }

    @Override
    public void remove(AbstractSelectableChannel channel) {
        SelectionKey key = channel.keyFor(_selector);
        key.cancel();
    }
}
