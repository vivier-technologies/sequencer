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

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.commands.CommandHeader;
import com.vivier_technologies.common.admin.AdminHandler;
import com.vivier_technologies.common.admin.AdminReceiver;
import com.vivier_technologies.common.admin.StatusEmitter;
import com.vivier_technologies.common.eventreceiver.EventHandler;
import com.vivier_technologies.common.eventreceiver.EventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.sequencer.commandreceiver.CommandHandler;
import com.vivier_technologies.sequencer.commandreceiver.CommandReceiver;
import com.vivier_technologies.sequencer.emitter.EventEmitter;
import com.vivier_technologies.sequencer.eventstore.EventStore;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.sequencer.replay.EventReplay;
import com.vivier_technologies.utils.ByteBufferSliceKeyIntMap;
import com.vivier_technologies.utils.ByteBufferSliceKeyObjectIntMap;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;

public class Sequencer implements CommandHandler, EventHandler, AdminHandler {

    private static final byte[] _loggingKey = Logger.generateLoggingKey("SEQUENCER");

    private final CommandReceiver _commandReceiver;
    private final EventReceiver _eventReceiver;
    private final CommandProcessor _processor;
    private final EventStore _eventStore;
    private final EventEmitter _eventEmitter;
    private final Multiplexer _mux;
    private final EventReplay _replayer;
    private final StatusEmitter _statusEmitter;
    private final AdminReceiver _adminReceiver;

    private final Logger _logger;

    private boolean _active = false;

    private final ByteBufferSliceKeyIntMap _map;

    @Inject
    public Sequencer(Logger logger, Configuration configuration, Multiplexer mux, CommandProcessor processor,
                     EventStore eventStore, EventEmitter eventEmitter, CommandReceiver commandReceiver,
                     EventReceiver eventReceiver, EventReplay replayer, StatusEmitter statusEmitter,
                     AdminReceiver adminReceiver) {

        String source = configuration.getString("source");
        if(source == null)
            throw new IllegalArgumentException("No source name set on sequencer");

        _logger = logger;
        _mux = mux;
        _processor = processor;
        _eventStore = eventStore;
        _eventEmitter = eventEmitter;
        _commandReceiver = commandReceiver;
        _eventReceiver = eventReceiver;
        _replayer = replayer;
        _statusEmitter = statusEmitter;
        _adminReceiver = adminReceiver;

        _adminReceiver.setHandler(this);
        _commandReceiver.setHandler(this);
        _eventReceiver.setHandler(this);

        _map = new ByteBufferSliceKeyObjectIntMap(
                configuration.getInt("sequencer.expectedcommandsenders", 50));
    }

    public CommandProcessor getProcessor() {
        return _processor;
    }

    //TODO implement scheduler on top of mux
    //TODO event storage and structures
    //TODO heartbeating
    //TODO event replay!!

    public void start() {
        try {
            _mux.open();
            _eventStore.open();
            _statusEmitter.open();
            _adminReceiver.open();

            _mux.run();
        } catch (IOException e) {
            _logger.error(_loggingKey, "Unable to start as cannot open dependent modules");
        }
    }

    public void stop() {
        // close receiver first
        _commandReceiver.close();
        _eventReceiver.close();
        _replayer.close();
        _eventEmitter.close();
        _statusEmitter.close();
        _eventStore.close();
        _adminReceiver.close();

        _mux.close();
    }

    /**
     * The sequencer receives a command from a sender and processes it - assuming its valid then puts it into its store
     * and publishes it out
     *
     * Will only be listening when active
     *
     * @param command the received command
     */
    @Override
    public void onCommand(Command command) {
        // Validate the command has the right sequence for the src
        if(_map.compareAndSetIfIncrement(
                command.getData(), CommandHeader.SRC, CommandHeader.SRC_LEN, command.getHeader().getSequence())) {
            // its good
            Event e = _processor.process(command);
            // TODO consider what to do when can't store and whether we should listen to event on the network - gives more common codepath..
            _eventStore.store(e);
            try {
                _eventEmitter.send(e);
            } catch (IOException ioException) {
                _logger.error(_loggingKey, "Unable to send event for command - clients will request replay from sequencer when they realise");
            }
        } else {
            _logger.info(_loggingKey, "Bad command sequence");
        }
    }

    /**
     * The sequencer receives an event from another sequencer instance thats primary and processes it putting it in its
     * store afterwards - all sequencers should have the same state - events cause the state to be updated blindly rather
     * than being validated as in the case of a command
     *
     * Will only be listening when passive
     *
     * @param event the received event from the stream
     */
    @Override
    public void onEvent(Event event) {
        Event e = _processor.process(event);
        // store here so backup sequencers can be a source of replay and limit impact to primary though repeater
        // will still be preferred source
        // TODO consider what to do when can't store...
        _eventStore.store(e);
    }

    // Very simple state active/passive so no need for state machine here thus far

    /**
     * Go active - becomes command processor and starts listening and generating events from those commands
     */
    @Override
    public void onGoActive() {
        if(!_active) {
            _logger.info(_loggingKey, "Going active");
            try {
                _commandReceiver.open();
                _eventEmitter.open();
                _active = true;
                if (_eventStore.isEmpty()) {
                    // TODO send out start of stream if its the start of the stream
                }
                _logger.info(_loggingKey, "Gone active");
                _statusEmitter.sendStatus(_active);
            } catch (IOException e) {
                _logger.error(_loggingKey, "Unable to go active as cannot open command receiver");
            }
        } else {
            _logger.info(_loggingKey, "Ignoring go active as not passive");
        }
    }

    /**
     * Go passive - becomes event listener and just blindly reads the stream
     */
    @Override
    public void onGoPassive() {
        if(_active) {
            _logger.info(_loggingKey, "Going passive");
            try {
                // start listening to events on assumption another sequencer is taking over
                _eventReceiver.open();
                // stop listening to commands
                _commandReceiver.close();
                _eventEmitter.close();
                _active = false;
                _logger.info(_loggingKey, "Gone passive");
                _statusEmitter.sendStatus(_active);
            } catch (IOException e) {
                _logger.error(_loggingKey, "Unable to go passive as cannot open event receiver");
            }
        } else {
            _logger.info(_loggingKey, "Ignoring go passive as not active");
        }
    }

    /**
     * Close down and if active send out the end of stream marker
     */
    @Override
    public void onShutdown() {
        _logger.info(_loggingKey, "Shutting down");
        if(_active) {
            // TODO send end of stream out
        }
        stop();
        _logger.info(_loggingKey, "Shutdown");
    }

    /**
     * Publish a status heartbeat
     */
    @Override
    public void onStatusRequest() {
        _statusEmitter.sendStatus(_active);
    }
}


