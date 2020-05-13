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

package com.vivier_technologies.events;

import com.vivier_technologies.commands.CommandHeader;

/**
 * Represents the header for a single sequenced event
 */
public interface EventHeader {

    int EVENT_LEN = 0;
    int TYPE = 4;
    int SRC = 6;
    int SRC_LEN = CommandHeader.SRC_LEN;
    int EVENT_SEQ = SRC + SRC_LEN;

    int EVENT_HEADER_LEN = EVENT_SEQ + 8;

    /**
     * Length of event
     *
     * @return length of event message
     */
    int getLength();

    /**
     * Event type
     *
     * @return type of event being sent/received
     */
    short getType();

    /**
     * Original source of the command that generated the event or in the case of certain events that are created
     * by the sequencer itself the sequencer source name
     *
     * @return string contained in byte array of length SRC_LEN
     */
    byte[] getSource();

    /**
     * Event sequence id - incrementing id maintained by the sequencer
     *
     * @return long representing the sequence id of this event
     */
    long getSequence();
}
