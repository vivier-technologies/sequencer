package com.vivier_technologies.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    @Test
    public void validateLoggingKeyRestrictionWorks() {
        assertThrows(IllegalArgumentException.class,
                () -> Logger.generateLoggingKey("WAY_TOO_LONG_FOR_THIS_TO_NOT_THROW"));
    }

    @Test
    public void validateLoggingKeyGeneratorWorks() {
        byte[] key = "TESTTEST".getBytes();
        assertEquals(new String(key), new String(Logger.generateLoggingKey(new String(key))));
    }

}