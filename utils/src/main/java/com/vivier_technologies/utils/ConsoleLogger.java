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

public class ConsoleLogger implements Logger {

    @Override
    public void info(byte[] component, String s) {
        log(Logger.INFO, component, s);
    }

    @Override
    public void info(byte[] component, String s1, String s2) {
        log(Logger.INFO, component, s1, s2);
    }

    @Override
    public void info(byte[] component, String s1, String s2, String s3) {
        log(Logger.INFO, component, s1+s2+s3);
    }

    @Override
    public void warn(byte[] component, String s) {
        log(Logger.WARN, component, s);
    }

    @Override
    public void warn(byte[] component, String s1, String s2) {
        log(Logger.WARN, component, s1, s2);
    }

    @Override
    public void error(byte[] component, String s) {
        log(Logger.ERROR, component, s);
    }

    @Override
    public void error(byte[] component, String s1, String s2) {
        log(Logger.ERROR, component, s1, s2);
    }

    @Override
    public void log(byte[] level, byte[] component, String s) {
        System.out.println(new String(level) + ": " + new String(component) + ": " + s);
    }

    @Override
    public void log(byte[] level, byte[] component, String s1, String s2) {
        log(level, component, s1+s2);
    }
}
