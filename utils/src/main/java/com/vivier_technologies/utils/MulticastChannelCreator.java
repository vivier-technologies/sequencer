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

/**
 * Bit contrived but really helpful for testing the network io classes..
 */
public interface MulticastChannelCreator {

    /**
     * Return a datagram channel that can handle multicast inbound
     *
     * @param ip ip to use locally
     * @param multicastAddress multicast address to listen on
     * @param multicastPort multicast port to listen on
     * @param multicastLoopback whether to listen to loopback traffic i.e. from processes on same machine
     * @param receiveBufferSize buffer size to hint to OS about
     * @param maxMessageSize max size of inbound message
     *
     * @return DatagramChannel
     *
     * @throws IOException if unable to setup the channel
     */
    DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                        boolean multicastLoopback, int receiveBufferSize,
                                        int maxMessageSize) throws IOException;

    /**
     * Return a datagram channel that can send multicast traffic
     *
     * @param ip ip to use locally
     * @param multicastAddress multicast address to send on
     * @param multicastPort multicast port to send on
     * @param multicastLoopback
     * @param sendBufferSize
     * @return
     * @throws IOException
     */
    DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
                                     boolean multicastLoopback, int sendBufferSize, int ttl) throws IOException;
}
