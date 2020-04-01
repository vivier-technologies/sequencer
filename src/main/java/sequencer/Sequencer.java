package sequencer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import processor.CommandProcessor;

public class Sequencer {

    private CommandProcessor _processor;

    public Sequencer(CommandProcessor processor) {
        _processor = processor;
    }

    public CommandProcessor getProcessor() {
        return _processor;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            Injector injector = Guice.createInjector(
                    (com.google.inject.Module) Class.forName(args[1]).getConstructor().newInstance());
            CommandProcessor processor = injector.getInstance(CommandProcessor.class);
            Sequencer sequencer = new Sequencer(processor);
        } else {
            // need better logger
            System.out.println("No processor defined");
        }

    }
}


