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

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteArrayUtils {

    /**
     * Copy from into to - assumes from is going to be fully copied and padded as necessary - no truncation
     *
     * @param from byte array to copy from
     * @param to byte array to copy into
     * @param toPosition position of where to start on to array
     * @param padToLength how long to pad out to
     * @param pad padding byte to use
     *
     * @throws IllegalArgumentException if destination too small or truncation would occur
     */
    public static void copyAndPadRight(byte[] from, byte[] to, int toPosition, int padToLength, byte pad) {
        if(from.length > (to.length - toPosition))
            throw new IllegalArgumentException("from array too large for destination");
        if(padToLength > (to.length - toPosition))
            throw new IllegalArgumentException("adding field of size with padding that is too large for destination");
        if(from.length > padToLength)
            throw new IllegalArgumentException("from array value would be truncated");
        System.arraycopy(from, 0, to, toPosition, from.length);
        if(from.length < padToLength) {
            // not a very efficient impl but ok for this purpose given frequency of call
            Arrays.fill(to, from.length, padToLength, pad);
        }
    }

    /**
     * Copy from into to padding with spaces as appropriate
     *
     * @param from byte array to copy from
     * @param to byte array to copy into
     * @param toPosition position of where to start on to array
     * @param padToLength how long to pad out to
     *
     * @throws IllegalArgumentException if destination too small or truncation would occur
     */
    public static void copyAndPadRightWithSpaces(byte[] from, byte[] to, int toPosition, int padToLength) {
        ByteArrayUtils.copyAndPadRight(from, to, toPosition, padToLength, (byte)' ');
    }

    /**
     * Copy from into to padding as appropriate or truncating if necessary
     *
     * @param from byte array to copy from
     * @param to byte array to copy into
     * @param toPosition position of where to start on to array
     * @param padToLength how long to pad out to
     * @param pad padding byte to use
     *
     * @throws IllegalArgumentException if destination too small
     */
    public static void copyAndTruncateOrPadRight(byte[] from, byte[] to, int toPosition, int padToLength, byte pad) {
        if(padToLength > (to.length - toPosition))
            throw new IllegalArgumentException("adding field of size with padding that is too large for destination");
        if(from.length < padToLength) {
            System.arraycopy(from, 0, to, toPosition, from.length);
            // not a very efficient impl but ok for this purpose given frequency of call
            Arrays.fill(to, from.length, padToLength, pad);
        } else {
            System.arraycopy(from, 0, to, toPosition, padToLength);
        }
    }

    /**
     * Copy from into to padding with spaces as appropriate or truncating if necessary
     *
     * @param from byte array to copy from
     * @param to byte array to copy into
     * @param toPosition position of where to start on to array
     * @param padToLength how long to pad out to
     *
     * @throws IllegalArgumentException if destination too small
     */
    public static void copyAndTruncateOrPadRightWithSpaces(byte[] from, byte[] to, int toPosition, int padToLength) {
        ByteArrayUtils.copyAndPadRight(from, to, toPosition, padToLength, (byte)' ');
    }

    /**
     * Straight copy with no upfront checks for whether to byte array has capacity - assumed callers know what to do
     * @param from byte buffer to copy from
     * @param fromPos bytebuffer pos to start copy
     * @param to byte array to copy to
     * @param toPos location in byte array to copy to
     * @param fromLength length of data to copy
     */
    public static void copy(ByteBuffer from, int fromPos, byte[] to , int toPos, int fromLength) {
        if(from.hasArray()) {
            // array copy much faster but only when backed by array i.e. on heap/non-direct
            System.arraycopy(from.array(), fromPos, to, toPos, fromLength);
        } else {
            from.get(fromPos, to, toPos, fromLength);
        }
    }
}
