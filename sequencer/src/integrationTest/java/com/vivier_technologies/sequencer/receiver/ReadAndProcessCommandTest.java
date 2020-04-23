package com.vivier_technologies.sequencer.receiver;

import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.StandardJVMMultiplexer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ReadAndProcessCommandTest {

    @Test
    public void testOnRead() throws Exception {
        CommandProcessor cp = Mockito.mock(CommandProcessor.class);
        Multiplexer mux = new StandardJVMMultiplexer(new ConsoleLogger());
        mux.open();
        MulticastCommandReceiver receiver = new MulticastCommandReceiver(null, mux, cp,
                "127.0.0.1", "239.0.0.0", 4000, true, 8192, 1472);
        receiver.open();
        //receiver.onRead();
        //TODO implement sender to complete the loop
    }
}
