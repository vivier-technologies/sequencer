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
import com.vivier_technologies.events.Event;

public interface CommandProcessor {

    /**
     * Called when the sequencer hosting the processor is active and the master for receiving commands
     * and publishing events
     * @param command
     * @return
     */
    Event process(Command command);

    /**
     * Called when the sequencer hosting the processor is passive and a slave of events
     * @param event
     * @return
     */
    Event process(Event event);

    String getName();
}
