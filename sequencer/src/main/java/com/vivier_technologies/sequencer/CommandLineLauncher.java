/*
 * Copyright 2020  vivier technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vivier_technologies.sequencer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.vivier_technologies.common.admin.AdminReceiver;
import com.vivier_technologies.common.admin.MulticastAdminReceiver;
import com.vivier_technologies.common.admin.MulticastStatusEmitter;
import com.vivier_technologies.common.admin.StatusEmitter;
import com.vivier_technologies.common.eventreceiver.EventReceiver;
import com.vivier_technologies.common.eventreceiver.MulticastEventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.StandardJVMMultiplexer;
import com.vivier_technologies.sequencer.commandreceiver.CommandReceiver;
import com.vivier_technologies.sequencer.commandreceiver.MulticastCommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.emitter.MulticastEventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.eventstore.InMemoryEventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.sequencer.replay.MulticastEventReplay;
import com.vivier_technologies.utils.*;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class CommandLineLauncher {

    private static final byte[] _componentName = Logger.generateLoggingKey("CL_LAUNCHER");

    public static void main(String[] args) {

        // TODO get logger type from config
        Logger logger = new ConsoleLogger();
        logger.info(CommandLineLauncher._componentName, "Sequencer starting");

        try {
            // TODO this configuration may create rather a lot of classes which may not be ideal but just getting going for now
            Configuration config = ConfigReader.getConfig(logger, args);
            // load different modules depending on options on command line
            // allow different command processors on command line to be plugged in
            try {
                final Class commandProcessorClass =
                        Class.forName(config.getString("commandprocessor",
                                "com.vivier_technologies.sequencer.processor.NoOpCommandProcessor"));

                // TODO guice may create rather a lot of classes which may not be ideal but just getting going for now
                Injector injector = Guice.createInjector(new AbstractModule() {

                    // TODO make more advanced with multiple modules etc for different setups but for now just get it going
                    @Override
                    protected void configure() {
                        // bit weak but not sure there is another way here
                        bind(CommandProcessor.class).to(
                                (Class<? extends CommandProcessor>) commandProcessorClass).asEagerSingleton();

                        bind(EventStore.class).to(InMemoryEventStore.class).asEagerSingleton();
                        bind(Multiplexer.class).to(StandardJVMMultiplexer.class).asEagerSingleton();
                        bind(MulticastChannelCreator.class).to(MulticastNetworkChannelCreator.class).asEagerSingleton();

                        bind(EventEmitter.class).to(MulticastEventEmitter.class);
                        bind(CommandReceiver.class).to(MulticastCommandReceiver.class);
                        bind(EventReceiver.class).to(MulticastEventReceiver.class);
                        bind(EventReplay.class).to(MulticastEventReplay.class);
                        bind(StatusEmitter.class).to(MulticastStatusEmitter.class);
                        bind(AdminReceiver.class).to(MulticastAdminReceiver.class);
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
