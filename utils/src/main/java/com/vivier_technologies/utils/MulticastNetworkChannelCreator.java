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

    @Override
    public final DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                               boolean multicastLoopback, int receiveBufferSize,
                                               int maxCommandSize) throws IOException {

        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        if(maxCommandSize > nif.getMTU() - MulticastNetworkChannelCreator.NETWORK_HEADERS)
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
                                            boolean multicastLoopback, int sendBufferSize) throws IOException {

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.configureBlocking(false);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, multicastLoopback);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, sendBufferSize);
        channel.bind(null); // select any local address
        channel.join(multicastAddress, nif);

        return channel;
    }

}
