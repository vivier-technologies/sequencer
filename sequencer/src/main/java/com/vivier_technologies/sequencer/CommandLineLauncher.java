package com.vivier_technologies.sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.emitter.MulticastEventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.eventstore.MemoryMappedEventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.receiver.CommandReceiver;
import com.vivier_technologies.sequencer.receiver.MulticastCommandReceiver;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.sequencer.replay.MulticastEventReplay;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.StandardJVMMultiplexer;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class CommandLineLauncher {

    private static final byte[] _componentName = Logger.generateLoggingKey("CL_LAUNCHER");

    public static void main(String[] args) {

        // TODO get from config
        Logger logger = new ConsoleLogger();
        logger.info(CommandLineLauncher._componentName, "Sequencer starting");

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

            logger.info(CommandLineLauncher._componentName, "Command line options:");
            for (Option option : commandLine.getOptions()) {
                logger.info(CommandLineLauncher._componentName, option.getArgName(), "=", option.getValue());
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
                                        "com.vivier_technologies.sequencer.processor.NoOpCommandProcessor"));
                        Injector injector = Guice.createInjector(new AbstractModule() {

                            // TODO make more advanced with multiple modules etc but for now just get it going
                            @Override
                            protected void configure() {
                                // bit weak but not sure there is another way here
                                bind(CommandProcessor.class).to(
                                        (Class<? extends CommandProcessor>) commandProcessorClass).asEagerSingleton();

                                bind(EventStore.class).to(MemoryMappedEventStore.class).asEagerSingleton();
                                bind(Multiplexer.class).to(StandardJVMMultiplexer.class).asEagerSingleton();

                                bind(EventEmitter.class).to(MulticastEventEmitter.class);
                                bind(CommandReceiver.class).to(MulticastCommandReceiver.class);
                                bind(EventReplay.class).to(MulticastEventReplay.class);
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
                        logger.error(CommandLineLauncher._componentName, "Unable to instantiate command sequencer.processor class, shutting down");
                    }
                } catch (ConfigurationException e) {
                    logger.error(CommandLineLauncher._componentName, "Unable to parse configuration, shutting down");
                }
            } else {
                logger.error(CommandLineLauncher._componentName, "invalid config type");
            }
        } catch (ParseException e) {
            logger.error(CommandLineLauncher._componentName, "Unable to parse command line, shutting down");
        }
    }
}
