package com.vivier_technologies.sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.vivier_technologies.common.admin.AdminReceiver;
import com.vivier_technologies.common.admin.StatusEmitter;
import com.vivier_technologies.common.admin.TestAdminReceiver;
import com.vivier_technologies.common.admin.TestStatusEmitter;
import com.vivier_technologies.common.eventreceiver.EventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.StandardJVMMultiplexer;
import com.vivier_technologies.sequencer.commandreceiver.CommandReceiver;
import com.vivier_technologies.sequencer.commandreceiver.TestCommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.emitter.TestEventEmitter;
import com.vivier_technologies.sequencer.eventreceiver.TestEventReceiver;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.eventstore.TestEventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.processor.TestCommandProcessor;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.sequencer.replay.MulticastEventReplay;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
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
                bind(EventReceiver.class).to(TestEventReceiver.class);
                bind(EventReplay.class).to(MulticastEventReplay.class);
                bind(StatusEmitter.class).to(TestStatusEmitter.class);
                bind(AdminReceiver.class).to(TestAdminReceiver.class);
            }

            @Provides
            Configuration provideConfiguration() {
                Configuration mock = Mockito.mock(Configuration.class);
                Mockito.when(mock.getString("source")).thenReturn("test");
                return mock;
            }
        });

        Sequencer s = injector.getInstance(Sequencer.class);
        assertEquals("Test", s.getProcessor().getName());
    }
}
