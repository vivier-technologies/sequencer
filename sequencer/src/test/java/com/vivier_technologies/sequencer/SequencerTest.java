package com.vivier_technologies.sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.emitter.TestEventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.eventstore.TestEventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.receiver.CommandReceiver;
import com.vivier_technologies.sequencer.receiver.TestCommandReceiver;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
import com.vivier_technologies.sequencer.processor.TestCommandProcessor;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.StandardJVMMultiplexer;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                return Mockito.mock(Configuration.class);
            }
        });

        Sequencer s = injector.getInstance(Sequencer.class);
        assertEquals("Test", s.getProcessor().getName());
    }
}
