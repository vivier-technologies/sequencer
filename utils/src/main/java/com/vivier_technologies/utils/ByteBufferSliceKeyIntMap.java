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

package com.vivier_technologies.utils;

import java.nio.ByteBuffer;

/**
 * No intention to make this extend Map - its a targeted implementation for
 * updating a int counter using a section of a bytebuffer as the key efficiently
 * in memory terms and maintaining hashmap performance characteristics
 *
 * Note the key sizes should be of consistent size and smaller is better (assuming hash is reasonable)
 *
 */
public interface ByteBufferSliceKeyIntMap {

    /**
     * The key slice indicated within the buffer compare the int passed in
     * is an +1 increment on the current value or 1 if not present
     *
     * @param key ByteBuffer containing the key data
     * @param keyStart position of start of key within the buffer
     * @param keyLength length of key within the buffer
     * @param value value to test and set
     *
     * @return true if valid int id has been passed in and the new map value corresponds to this value
     * otherwise false
     */
    boolean compareAndSetIfIncrement(ByteBuffer key, int keyStart, int keyLength, int value);

}
