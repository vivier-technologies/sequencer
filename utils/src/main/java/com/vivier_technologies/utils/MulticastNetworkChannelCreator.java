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
import java.net.*;
import java.nio.channels.DatagramChannel;

public class MulticastNetworkChannelCreator implements MulticastChannelCreator {

    private static final int NETWORK_HEADERS = 8+20; // UDP and IP respectively

    /**
     * Create a datagram channel with the maximum size verified vs the MTU
     *
     * @param ip ip to use locally
     * @param multicastAddress multicast address to listen on
     * @param multicastPort multicast port to listen on
     * @param multicastLoopback whether to listen to loopback traffic i.e. from processes on same machine
     * @param receiveBufferSize buffer size to hint to OS about
     * @param maxMessageSize max size of inbound message
     *
     * @return DatagramChannel setup to receive multicast
     *
     * @throws IOException if unable to create and configure channel
     */
    @Override
    public final DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                               boolean multicastLoopback, int receiveBufferSize,
                                               int maxMessageSize) throws IOException {

        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        if(maxMessageSize > nif.getMTU() - MulticastNetworkChannelCreator.NETWORK_HEADERS)
            throw new IllegalArgumentException("Max message size set too large for MTU");

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.configureBlocking(false);

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, multicastLoopback);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, receiveBufferSize);

        channel.bind(new InetSocketAddress(multicastPort));
        channel.join(InetAddress.getByName(multicastAddress), nif);

        return channel;
    }

    @Override
    public final DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
                                            boolean multicastLoopback, int sendBufferSize, int ttl) throws IOException {

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.configureBlocking(false);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, multicastLoopback);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, sendBufferSize);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ttl);
        channel.bind(null); // select any local address
        channel.join(multicastAddress, nif);

        return channel;
    }

}
