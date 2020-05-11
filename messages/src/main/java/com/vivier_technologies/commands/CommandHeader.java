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

    int CMD_LEN = 0;
    int TYPE = 4;
    int SRC = 6;
    int SRC_LEN = 8;
    int CMD_SEQ = SRC + SRC_LEN;

    int CMD_HEADER_LEN = CMD_SEQ + 4;

    int getLength();

    short getType();

    byte[] getSource();

    // unlikely to ever have a single sender put out more than 2^31-1 messages so deliberately using int
    int getSequence();

    void setHeader(int length, short type, byte[] src, int sequence);

}
