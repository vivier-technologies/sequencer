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
import com.vivier_technologies.utils.*;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class CommandLineLauncher {

    private static final byte[] _componentName = Logger.generateLoggingKey("CL_LAUNCHER");

    public static void main(String[] args) {

        // TODO get from config
        Logger logger = new ConsoleLogger();
        logger.info(CommandLineLauncher._componentName, "Sequencer starting");

        try {
            Configuration config = ConfigReader.getConfig(logger, args);
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
        } catch (ParseException e) {
            logger.error(CommandLineLauncher._componentName, "Unable to parse command line, shutting down");
        } catch (ConfigurationException e) {
            logger.error(CommandLineLauncher._componentName, "Unable to parse configuration, shutting down");
        }
    }
}
