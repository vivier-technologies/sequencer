package sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import sequencer.commands.CommandReceiver;
import sequencer.commands.MulticastCommandReceiver;
import sequencer.processor.CommandProcessor;
import sequencer.processor.TestCommandProcessor;
import sequencer.events.EventEmitter;
import sequencer.events.TestEventEmitter;
import sequencer.eventstore.EventStore;
import sequencer.eventstore.TestEventStore;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequencerTest {

    @Test
    public void testInstantiateWorks() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CommandProcessor.class).to(TestCommandProcessor.class);
                bind(EventStore.class).to(TestEventStore.class);
                bind(EventEmitter.class).to(TestEventEmitter.class);
                bind(CommandReceiver.class).to(MulticastCommandReceiver.class);
            }
        });
        Sequencer s = injector.getInstance(Sequencer.class);
        assertEquals("Test", s.getProcessor().getName());
    }
}
