package com.vivier_technologies.utils;

/**
 * Intention to write an object efficient logger behind this eventually rather than inefficient java one
 * that can push out to variety of locations - file, indexing service etc
 */
public interface Logger {

    byte[] WARN = "WARN".getBytes();
    byte[] ERROR = "ERROR".getBytes();
    byte[] INFO = "INFO".getBytes();

    void info(byte[] component, String s);

    void info(byte[] component, String s1, String s2);

    void info(byte[] component, String s1, String s2, String s3);

    void warn(byte[] component, String s);

    void warn(byte[] component, String s1, String s2);

    void error(byte[] component, String s);

    void error(byte[] component, String s1, String s2);

    void log(byte[] level, byte[] component, String s);

    void log(byte[] level, byte[] component, String s1, String s2);
}
