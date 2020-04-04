package sequencer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.*;
import processor.CommandProcessor;
import sequencer.events.EventEmitter;
import sequencer.eventstore.EventStore;
import sequencer.utils.ConsoleLogger;
import sequencer.utils.Logger;

public class Sequencer {

    private static final byte[] _componentName = "SEQUENCER".getBytes();

    private CommandProcessor _processor;

    public Sequencer(final CommandProcessor processor, final EventStore eventStore, final EventEmitter emitter) {
        _processor = processor;
    }

    public final CommandProcessor getProcessor() {
        return _processor;
    }

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(OptionBuilder
                .withArgName("type")
                .hasArg()
                .withDescription("Whether to initialise using file based or url based config")
                .create("configtype"));
        options.addOption(OptionBuilder
                .withArgName("name")
                .hasArg()
                .withDescription("Either a url to the properties file or a config file")
                .create("config"));
        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, args);

        // TODO get from config
        Logger logger = new ConsoleLogger();
        logger.log(Sequencer._componentName, "Sequencer starting");
        for (Option option : commandLine.getOptions()) {
            logger.log(Sequencer._componentName, option.getArgName(), option.getValue());
        }

//        Injector injector = Guice.createInjector(
//                (com.google.inject.Module) Class.forName(args[1]).getConstructor().newInstance());
//        CommandProcessor processor = injector.getInstance(CommandProcessor.class);
        Sequencer sequencer = new Sequencer(null, null, null);
    }
}


