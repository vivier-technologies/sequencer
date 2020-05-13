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

package com.vivier_technologies.sequencer.processor;

import com.vivier_technologies.commands.Command;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;

public class NoOpCommandProcessor implements CommandProcessor {
    private static final byte[] _loggingKey = Logger.generateLoggingKey("NOOPCMDPROC");

    private final Logger _logger;
    private final ByteBufferEvent _event;

    @Inject
    public NoOpCommandProcessor(Logger logger, Configuration configuration) {
        _logger = logger;

        _event = new ByteBufferEvent();
    }

    @Override
    public final Event process(Command command) {
        _event.setData(command.getData());
        _logger.info(_loggingKey, "Received command - processing");
        return _event;
    }

    @Override
    public Event process(Event event) {
        return null;
    }

    @Override
    public final String getName() {
        return null;
    }
}
