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
    public void testPadWithSpaces() {
        byte[] from = "TEST".getBytes();
        byte[] to = new byte[20];
        ByteArrayUtils.copyAndPadRightWithSpaces(from, to, 0, 8);
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
    public void testTooLongAndWouldBeTruncated() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[20];
        assertThrows(IllegalArgumentException.class,
                () -> ByteArrayUtils.copyAndPadRight(from, to, 0, 8, (byte)' '));
    }

    @Test
    public void testTooLongAndWillNotFit() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[8];
        assertThrows(IllegalArgumentException.class,
                () -> ByteArrayUtils.copyAndPadRight(from, to, 0, 12, (byte)' '));
    }

    @Test
    public void testTooLongAndWillNotFitFromPosition() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[12];
        assertThrows(IllegalArgumentException.class,
                () -> ByteArrayUtils.copyAndPadRight(from, to, 4, 8, (byte)' '));
    }

    @Test
    public void testTooLongAndTruncated() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[10];
        ByteArrayUtils.copyAndTruncateOrPadRight(from, to, 0, 10, (byte)' ');
        assertEquals(new String("TESTTESTTE"), new String(to, 0, 10));
    }

    @Test
    public void testTooLongForDestination() {
        byte[] from = "TESTTESTTEST".getBytes();
        byte[] to = new byte[10];
        assertThrows(IllegalArgumentException.class,
                () -> ByteArrayUtils.copyAndTruncateOrPadRight(from, to, 0, 12, (byte)' '));
    }

    @Test
    public void testCopyNonDirect() {
        ByteBuffer from = ByteBuffer.wrap("TESTTESTTEST".getBytes());
        byte[] to = new byte[12];
        ByteArrayUtils.copy(from, 0, to, 0, from.limit());
        assertEquals(new String("TESTTESTTEST"), new String(to, 0, 12));
    }

    @Test
    public void testCopyDirect() {
        ByteBuffer from = ByteBuffer.allocateDirect(12);
        from.put("TESTTESTTEST".getBytes());
        byte[] to = new byte[12];
        ByteArrayUtils.copy(from, 0, to, 0, from.limit());
        assertEquals(new String("TESTTESTTEST"), new String(to, 0, 12));
    }
}