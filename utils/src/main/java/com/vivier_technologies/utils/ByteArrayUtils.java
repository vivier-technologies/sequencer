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

import java.util.Arrays;

public class ByteArrayUtils {

    /**
     * Copy from into to - assumes from is going to be fully copied
     * @param from
     * @param to
     * @param toPosition
     * @param padToLength
     * @param pad
     */
    public static void copyAndPadRight(byte[] from, byte[] to, int toPosition, int padToLength, byte pad) {
        if(from.length > to.length)
            throw new IllegalArgumentException("from array too large for destination");
        System.arraycopy(from, 0, to, toPosition, from.length);
        if(from.length < padToLength) {
            // not a very efficient impl but ok for this purpose given frequency of call
            Arrays.fill(to, from.length, padToLength, pad);
        }
    }

    /**
     * Copy from into to padding with spaces as appropriate
     *
     * @param from
     * @param to
     * @param toPosition
     * @param padToLength
     */
    public static void copyAndPadRightWithSpaces(byte[] from, byte[] to, int toPosition, int padToLength) {
        ByteArrayUtils.copyAndPadRight(from, to, toPosition, padToLength, (byte)' ');
    }
}
