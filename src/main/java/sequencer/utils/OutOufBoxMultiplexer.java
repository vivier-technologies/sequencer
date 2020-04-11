package sequencer.utils;

import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * Uses standard VM selector implementation which generates quite a bit of garbage..
 */
public class OutOufBoxMultiplexer implements Multiplexer, Scheduler {
    private Selector _selector;
    private Logger _logger;
    private boolean _run = true;

    @Override
    public boolean schedule(SchedulerListener listener) {
        return false;
    }

    @Inject
    public OutOufBoxMultiplexer(Configuration config, Logger logger) {

    }

    public void run() throws IOException {
        _selector = SelectorProvider.provider().openSelector();

        while(_run) {
            // TODO work out the timeout logic here
            long now = System.currentTimeMillis();
            if(_selector.selectNow() > 0) {
                // this is a
                Set<SelectionKey> keys = _selector.selectedKeys();
                Iterator iterator = keys.iterator();
                while(iterator.hasNext()) {
                    // whats ready

                    iterator.remove();
                }
            }
        }
        _selector.close();
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
        // TODO use a registration id to make this more efficient
        SelectionKey key = channel.keyFor(_selector);
        key.cancel();
    }
}
