package sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import processor.CommandProcessor;
import processor.TestCommandProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequencerTest {

    @Test
    public void testInstantiateWorks() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CommandProcessor.class).to(TestCommandProcessor.class);
            }
        });
        Sequencer s = new Sequencer(injector.getInstance(CommandProcessor.class));
        assertEquals("Test", s.getProcessor().getName());
    }
}
