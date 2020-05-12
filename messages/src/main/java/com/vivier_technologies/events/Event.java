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

import java.nio.ByteBuffer;

/**
 * Represents a single sequenced event
 */
public interface Event {

    int EVENT_BODY_START = EventHeader.EVENT_HEADER_LEN;

    /**
     * Get wrapper object to allow access to event header fields
     *
     * Mutable so don't store as a reference anywhere and should be used transiently
     *
     * @return object representing the event header
     */
    EventHeader getHeader();

    /**
     * Get underlying data
     *
     * @return buffer with data
     */
    ByteBuffer getData(); //TODO consider whether bytebuffer is the right abstraction here
}
