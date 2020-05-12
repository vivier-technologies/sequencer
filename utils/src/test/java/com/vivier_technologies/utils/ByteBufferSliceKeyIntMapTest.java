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

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByteBufferSliceKeyIntMapTest {

    @Test
    public void testCompareAndSetWithKey() {
        ByteBufferSliceKeyIntMap map = new ByteBufferSliceKeyObjectIntMap(10);
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 8, 3));
        assertTrue(
                map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 8, 1));
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 8, 3));
        assertTrue(
                map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 8, 2));
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 0, 8, 3));
        assertTrue(
                map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 0, 8, 1));
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 0, 8, 3));
        assertTrue(
                map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 0, 8, 2));
    }

    @Test
    public void testCompareAndSetWithLargeKey() {
        ByteBufferSliceKeyIntMap map = new ByteBufferSliceKeyObjectIntMap(10);
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 4, 3));
        assertTrue(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 4, 1));
        assertTrue(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTEST".getBytes()), 0, 4, 2));
        assertFalse(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 4, 4, 3));
        assertTrue(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 4, 4, 1));
        assertTrue(map.compareAndSetIfIncrement(ByteBuffer.wrap("TESTTES1".getBytes()), 4, 4, 2));
    }
}
