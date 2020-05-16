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

package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.Event;

import java.io.IOException;

/**
 * Very simple event store for the sequencer - intention is that the sequencer shouldn't be responding to event sequence
 * replay requests very often at all - instead the repeaters should be used as TCP going to be a better paradigm to transfer
 * a stream of events back to a client
 */
public interface EventStore {

    /**
     * Open the store - some implementations may retrieve the event information from disk
     *
     * @throws IOException if unable to open
     */
    void open() throws IOException;

    /**
     * Persist an event into the store
     *
     * @param event event to store
     * @return whether event was stored successfully
     */
    boolean store(Event event);

    /**
     * Retrieve a single event from the store
     * @param sequence retrieve the event corresponding to this sequence
     * @return the event in question
     */
    Event retrieve(long sequence);

    /**
     * Is the event store empty or not
     *
     * @return state of the store
     */
    boolean isEmpty();

    /**
     * Close the store - on some implementations this may store down to disk
     */
    void close();

}
