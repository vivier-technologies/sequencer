package com.vivier_technologies.sequencer.receiver;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.StandardJVMMultiplexer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ReadAndProcessCommandTest {

    @Test
    public void testOnRead() throws Exception {
        Multiplexer mux = new StandardJVMMultiplexer(new ConsoleLogger());
        mux.open();
        MulticastCommandReceiver receiver = new MulticastCommandReceiver(null, mux,
                "127.0.0.1", "239.0.0.0", 4000, true, 8192, 1472);
        CommandListener listener = Mockito.mock(CommandListener.class);
        receiver.setListener(listener);
        receiver.open();
        receiver.onRead();
        ArgumentCaptor<ByteBufferCommand> argumentCaptor = ArgumentCaptor.forClass(ByteBufferCommand.class);
        Mockito.verify(listener).onCommand(argumentCaptor.capture());
    }
}
