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

package com.vivier_technologies.commands;

import com.vivier_technologies.utils.ByteArrayUtils;

public interface CommandHeader {

    int CMD_LEN = 0;
    int TYPE = 4;
    int SRC = 6;
    int SRC_LEN = 8;
    int CMD_SEQ = SRC + SRC_LEN;

    int CMD_HEADER_LEN = CMD_SEQ + 4;

    /**
     * Make sure source name fits within the size allotted - shouldn't be called on the critical path
     * of any sensitive code
     *
     * @param s source name
     * @return validated source as a byte array
     */
    static byte[] validateSource(String s) {
        if(s.length() > SRC_LEN)
            throw new IllegalArgumentException("Source is limited to 8 bytes");
        byte[] source = new byte[SRC_LEN];
        ByteArrayUtils.copyAndPadRightWithSpaces(s.getBytes(), source, 0, SRC_LEN);
        return source;
    }

    /**
     * Length of command
     *
     * @return length of command message
     */
    int getLength();

    /**
     * Command type
     *
     * @return type of command being sent/received
     */
    short getType();

    /**
     * Logical source of the command sender - can have two physical instances with same source id but only
     * one should be live at a given point in time
     *
     * @return name of source as byte array of length SRC_LEN
     */
    byte[] getSource();

    /**
     * The command sequence - used to detect whether the command sender is caught up with the stream
     *
     * Commands with the wrong sequence - gapped i.e. not +1 or less than current sequence will not be processed
     *
     * @return command sequencer number
     */
    int getSequence();

    /**
     * Set the header on the command
     *
     * @param length length of the command
     * @param type type of the command
     * @param src logical sender source - can have multiple physical sources sending for availability purposes
     * @param sequence command sequence - needs to be +1 on prior command sequence
     */
    void setHeader(int length, short type, byte[] src, int sequence);

}
