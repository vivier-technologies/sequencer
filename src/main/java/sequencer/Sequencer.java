package sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import sequencer.commands.Command;
import sequencer.commands.CommandReceiver;
import sequencer.commands.UnicastCommandReceiver;
import sequencer.events.Event;
import sequencer.processor.CommandProcessor;
import sequencer.events.EventEmitter;
import sequencer.events.MulticastEventEmitter;
import sequencer.eventstore.EventStore;
import sequencer.eventstore.MemoryMappedEventStore;
import sequencer.utils.ConsoleLogger;
import sequencer.utils.Logger;
import sequencer.utils.Multiplexer;
import sequencer.utils.OutOufBoxMultiplexer;

import javax.inject.Inject;
import java.io.File;

public class Sequencer {

    private static final byte[] _componentName = "SEQUENCER".getBytes();

    private final CommandReceiver _receiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _emitter;

    @Inject
    public Sequencer(CommandProcessor processor, EventStore eventStore, EventEmitter emitter, CommandReceiver receiver) {
        _processor = processor;
        _eventStore = eventStore;
        _emitter = emitter;
        _receiver = receiver;
    }

    public final CommandProcessor getProcessor() {
        return _processor;
    }

    //TODO implement selector
    //TODO implement scheduler on top of mux
    //TODO event storage and structures
    //TODO command receiving
    //TODO heartbeating
    //TODO event replay
    public void start() {

    }

    public void process() {
        // probably called back from multiplexer in reality
        Command c = _receiver.receive();
        Event e = _processor.process(c);
        _eventStore.store(e);
    }

    public static void main(String[] args) {

        // TODO get from config
        Logger logger = new ConsoleLogger();
        logger.info(Sequencer._componentName, "Sequencer starting");

        Options options = new Options();
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("type")
                .hasArg()
                .withDescription("Whether to initialise using file based or url based config")
                .isRequired()
                .create("configtype"));
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("name")
                .hasArg()
                .withDescription("Either a url to the properties file or a config file")
                .isRequired()
                .create("config"));
        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);

            logger.info(Sequencer._componentName, "Command line options:");
            for (Option option : commandLine.getOptions()) {
                logger.info(Sequencer._componentName, option.getArgName(), "=", option.getValue());
            }

            // TODO add code to download from remote URL
            if (commandLine.getOptionValue("configtype").equalsIgnoreCase("file")) {
                Configurations configs = new Configurations();
                try {
                    Configuration config = configs.properties(new File(commandLine.getOptionValue("config")));

                    // load different modules depending on options on command line
                    // allow different command processors on command line to be plugged in
                    try {
                        final Class commandProcessorClass =
                                Class.forName(config.getString("sequencer.commandprocessor",
                                        "sequencer.processor.NoOpCommandProcessor"));
                        Injector injector = Guice.createInjector(new AbstractModule() {

                            @Override
                            protected void configure() {
                                // bit weak but not sure there is another way here
                                bind(CommandProcessor.class).to((Class<? extends CommandProcessor>) commandProcessorClass);

                                bind(EventStore.class).to(MemoryMappedEventStore.class);
                                bind(EventEmitter.class).to(MulticastEventEmitter.class);
                                bind(CommandReceiver.class).to(UnicastCommandReceiver.class);

                                bind(Logger.class).to(ConsoleLogger.class).asEagerSingleton();
                                bind(Multiplexer.class).to(OutOufBoxMultiplexer.class).asEagerSingleton();
                            }

                            @Provides
                            Configuration provideConfiguration() {
                                return config;
                            }

                        });

                        Sequencer sequencer = injector.getInstance(Sequencer.class);
                        sequencer.start();

                    } catch (ClassNotFoundException e) {
                        logger.error(Sequencer._componentName, "Unable to instantiate command sequencer.processor class, shutting down");
                    }
                } catch (ConfigurationException e) {
                    logger.error(Sequencer._componentName, "Unable to parse configuration, shutting down");
                }
            } else {
                logger.error(Sequencer._componentName, "invalid config type");
            }
        } catch (ParseException e) {
            logger.error(Sequencer._componentName, "Unable to parse command line, shutting down");
        }
    }
}


