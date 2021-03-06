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

public interface Status {

    interface State {
        byte ACTIVE = 0;
        byte PASSIVE = 1;
    }

    int STATE = 0;
    int INSTANCE_NAME = 1;
    int INSTANCE_NAME_LEN = 16;
    int MACHINE_NAME = INSTANCE_NAME + INSTANCE_NAME_LEN;
    int MACHINE_NAME_LEN = 50;
    int STATUS_LEN = MACHINE_NAME + MACHINE_NAME_LEN;

    /**
     * Get state of the status sender
     *
     * @return byte representing the state - values above
     */
    byte getState();

    /**
     * Physical instance name of the sending process
     *
     * @return string in byte array of length INSTANCE_NAME_LEN
     */
    byte[] getInstanceName();

    /**
     * Machine name the sending process is located on
     *
     * @return string in byte array of length MACHINE_NAME_LEN
     */
    byte[] getMachineName();
}
