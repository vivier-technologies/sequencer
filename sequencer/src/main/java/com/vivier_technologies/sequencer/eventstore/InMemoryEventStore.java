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

public class InMemoryEventStore implements EventStore {
    @Override
    public void open() throws IOException {

    }

    @Override
    public boolean store(Event event) {
        return false;
    }

    @Override
    public Events retrieve(long start, long end) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
