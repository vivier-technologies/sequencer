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

package com.vivier_technologies.admin;

import com.vivier_technologies.utils.ByteArrayUtils;

public interface Command {

    interface Type {
        short STATUS = 0;
        short GO_ACTIVE = 1;
        short GO_PASSIVE = 2;
        short SHUTDOWN = 3;
    }

    int TYPE = 0;
    int INSTANCE_NAME = 2;
    int INSTANCE_NAME_LEN = 16;
    int ADMIN_CMD_LEN = INSTANCE_NAME + INSTANCE_NAME_LEN;

    byte[] ALL_INSTANCES = validateInstanceName("*");

    /**
     * Make sure instance name fits within the size allotted - shouldn't be called on the critical path
     * of any sensitive code
     *
     * @param s instance name
     * @return validated instance name as a byte array
     */
    static byte[] validateInstanceName(String s) {
        if(s.length() > INSTANCE_NAME_LEN)
            throw new IllegalArgumentException("Instance name is limited to 16 bytes");
        byte[] instanceName = new byte[INSTANCE_NAME_LEN];
        ByteArrayUtils.copyAndPadRightWithSpaces(s.getBytes(), instanceName, 0, INSTANCE_NAME_LEN);
        return instanceName;
    }

    /**
     * Physical instance being address by the admin command
     *
     * @return string within byte array of length INSTANCE_NAME_LEN
     */
    byte[] getInstance();

    /**
     * Type of admin command
     *
     * @return short representing the type
     */
    short getType();
}
