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

/**
 * Intention to write an object efficient logger behind this eventually rather than inefficient java one
 * that can push out to variety of locations - file, indexing service etc
 */
public interface Logger {

    byte[] WARN = "WARN".getBytes();
    byte[] ERROR = "ERROR".getBytes();
    byte[] INFO = "INFO".getBytes();

    static byte[] generateLoggingKey(String s) {
        if(s.length() > 16)
            throw new IllegalArgumentException("Logging key limited to 16 bytes");
        return s.getBytes();
    }

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
