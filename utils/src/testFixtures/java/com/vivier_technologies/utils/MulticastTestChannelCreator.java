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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

public class MulticastTestChannelCreator implements MulticastChannelCreator {

    private DatagramChannel _receiveChannel;
    private DatagramChannel _sendChannel;

    public void setReceiveChannel(DatagramChannel receiveChannel) {
        _receiveChannel = receiveChannel;
    }

    public void setSendChannel(DatagramChannel sendChannel) {
        _sendChannel = sendChannel;
    }

    @Override
    public DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                               boolean multicastLoopback, int receiveBufferSize, int maxMessageSize)
            throws IOException {
        return _receiveChannel;
    }

    @Override
    public DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
                                            boolean multicastLoopback, int sendBufferSize, int ttl) throws IOException {
        return _sendChannel;
    }
}