package com.vivier_technologies.sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import com.vivier_technologies.sequencer.receiver.CommandReceiver;
import com.vivier_technologies.sequencer.receiver.MulticastCommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.emitter.MulticastEventEmitter;
import com.vivier_technologies.sequencer.eventstore.MemoryMappedEventStore;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.StandardJVMMultiplexer;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class Sequencer {

    private static final byte[] _componentName = "SEQUENCER".getBytes();

    private final CommandReceiver _receiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _emitter;
    private final Multiplexer _mux;
    private final Logger _logger;

    @Inject
    public Sequencer(Logger logger, Multiplexer mux, CommandProcessor processor,
                     EventStore eventStore, EventEmitter emitter, CommandReceiver receiver) {

        _logger = logger;
        _mux = mux;
        _processor = processor;
        _eventStore = eventStore;
        _emitter = emitter;
        _receiver = receiver;
    }

    public final CommandProcessor getProcessor() {
        return _processor;
    }

    //TODO implement scheduler on top of mux
    //TODO event storage and structures
    //TODO heartbeating
    //TODO event replay!!
    //TODO handle command retries or assume client will back off?
    public void start() {
        try {
            _mux.open();
            _emitter.open();
            _receiver.open();
            _eventStore.open();

            _mux.run();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to start as cannot open dependent modules");
        }
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
                try {
                    Configuration config =
                            new Configurations().properties(new File(commandLine.getOptionValue("config")));

                    // load different modules depending on options on command line
                    // allow different command processors on command line to be plugged in
                    try {
                        final Class commandProcessorClass =
                                Class.forName(config.getString("sequencer.commandprocessor",
                                        "sequencer.processor.NoOpCommandProcessor"));
                        Injector injector = Guice.createInjector(new AbstractModule() {

                            // TODO make more advanced with multiple modules etc but for now just get it going
                            @Override
                            protected void configure() {
                                // bit weak but not sure there is another way here
                                bind(CommandProcessor.class).to(
                                        (Class<? extends CommandProcessor>) commandProcessorClass).asEagerSingleton();

                                bind(EventStore.class).to(MemoryMappedEventStore.class);
                                bind(EventEmitter.class).to(MulticastEventEmitter.class);
                                bind(CommandReceiver.class).to(MulticastCommandReceiver.class);
                                bind(Multiplexer.class).to(StandardJVMMultiplexer.class).asEagerSingleton();
                            }

                            @Provides
                            Configuration provideConfiguration() {
                                return config;
                            }

                            @Provides
                            Logger provideLogger() {
                                return logger;
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


