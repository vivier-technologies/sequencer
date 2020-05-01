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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteArrayUtilsTest {

    @Test
    public void testPad() {
        byte[] from = "TEST".getBytes();
        byte[] to = new byte[20];
        ByteArrayUtils.copyAndPadRight(from, to, 0, 8, (byte)' ');
        assertEquals(new String("TEST    "), new String(to, 0, 8));
    }

    @Test
    public void testNoPad() {
        byte[] from = "TESTTEST".getBytes();
        byte[] to = new byte[20];
        ByteArrayUtils.copyAndPadRight(from, to, 0, 8, (byte)' ');
        assertEquals(new String("TESTTEST"), new String(to, 0, 8));
    }

    @Test
    public void testTooLongAndTruncate() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[20];
        ByteArrayUtils.copyAndPadRight(from, to, 0, 8, (byte)' ');
        assertEquals(new String("TESTTEST"), new String(to, 0, 8));
    }

    @Test
    public void testTooLongAndWillNotFit() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[8];
        assertThrows(IllegalArgumentException.class,
                () -> ByteArrayUtils.copyAndPadRight(from, to, 0, 8, (byte)' '));
    }

}