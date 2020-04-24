package com.vivier_technologies.utils;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

public class MulticastUtils {

    private static final int NETWORK_HEADERS = 8+20; // UDP and IP respectively

    public static DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                                      boolean multicastLoopback, int receiveBufferSize,
                                                      int maxCommandSize) throws IOException {

        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        if(maxCommandSize > nif.getMTU() - NETWORK_HEADERS)
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
    
    public static DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
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
