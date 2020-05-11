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
 * Very very inefficient logger that dumps to system.out - could improve dramatically but just meant to for playground
 * as the log info should go into remote stack for capture and easy analysis
 */
public class ConsoleLogger implements Logger {

    @Override
    public void info(byte[] loggingKey, String s) {
        log(Logger.INFO, loggingKey, s);
    }

    @Override
    public void info(byte[] loggingKey, String s1, String s2) {
        log(Logger.INFO, loggingKey, s1, s2);
    }

    @Override
    public void info(byte[] loggingKey, String s1, String s2, String s3) {
        log(Logger.INFO, loggingKey, s1, s2, s3);
    }

    @Override
    public void info(byte[] loggingKey, String s1, byte[] s2, byte[] s3) {
        log(Logger.INFO, loggingKey, s1, s2, s3);
    }

    @Override
    public void warn(byte[] loggingKey, String s) {
        log(Logger.WARN, loggingKey, s);
    }

    @Override
    public void warn(byte[] loggingKey, String s1, String s2) {
        log(Logger.WARN, loggingKey, s1, s2);
    }

    @Override
    public void error(byte[] loggingKey, String s) {
        log(Logger.ERROR, loggingKey, s);
    }

    @Override
    public void error(byte[] loggingKey, String s1, String s2) {
        log(Logger.ERROR, loggingKey, s1, s2);
    }

    @Override
    public void info(byte[] loggingKey, byte[] s1, byte[] s2) {
        log(Logger.INFO, loggingKey, s1, s2);
    }

    @Override
    public void log(byte[] level, byte[] loggingKey, String s) {
        // clearly horribly inefficient but this logger ain't intended to be used anywhere important like production
        System.out.println(new String(level) + ": " + new String(loggingKey) + ": " + s);
    }

    @Override
    public void log(byte[] level, byte[] loggingKey, String s1, String s2) {
        // clearly horribly inefficient but this logger ain't intended to be used anywhere important like production
        log(level, loggingKey, s1 + " " + s2);
    }

    private void log(byte[] level, byte[] loggingKey, String s1, String s2, String s3) {
        // clearly horribly inefficient but this logger ain't intended to be used anywhere important like production
        log(level, loggingKey, s1, s2 + " " + s3);
    }

    private void log(byte[] level, byte[] loggingKey, byte[] s1, byte[] s2) {
        // clearly horribly inefficient but this logger ain't intended to be used anywhere important like production
        log(level, loggingKey, new String(s1), new String(s2));
    }

    private void log(byte[] level, byte[] loggingKey, String s1, byte[] s2, byte[] s3) {
        // clearly horribly inefficient but this logger ain't intended to be used anywhere important like production
        log(level, loggingKey, s1, new String(s2), new String(s3));
    }

}
