package sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
import sequencer.commands.CommandReceiver;
import sequencer.commands.TestCommandReceiver;
import sequencer.events.EventEmitter;
import sequencer.events.TestEventEmitter;
import sequencer.eventstore.EventStore;
import sequencer.eventstore.TestEventStore;
import sequencer.processor.CommandProcessor;
import sequencer.processor.TestCommandProcessor;
import sequencer.utils.ConsoleLogger;
import sequencer.utils.Logger;
import sequencer.utils.Multiplexer;
import sequencer.utils.StandardJVMMultiplexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SequencerTest {

    @Test
    public void testInstantiateWorks() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Logger.class).to(ConsoleLogger.class).asEagerSingleton();
                bind(Multiplexer.class).to(StandardJVMMultiplexer.class).asEagerSingleton();
                bind(CommandProcessor.class).to(TestCommandProcessor.class);
                bind(EventStore.class).to(TestEventStore.class);
                bind(EventEmitter.class).to(TestEventEmitter.class);
                bind(CommandReceiver.class).to(TestCommandReceiver.class);
            }

            @Provides
            Configuration provideConfiguration() {
                return mock(Configuration.class);
            }
        });

        Sequencer s = injector.getInstance(Sequencer.class);
        assertEquals("Test", s.getProcessor().getName());
    }
}
