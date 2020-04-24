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