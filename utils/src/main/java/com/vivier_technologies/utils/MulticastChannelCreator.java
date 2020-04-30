package com.vivier_technologies.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

/**
 * Bit contrived but really helpful for testing the network io classes..
 */
public interface MulticastChannelCreator {

    DatagramChannel setupReceiveChannel(String ip, String multicastAddress, int multicastPort,
                                        boolean multicastLoopback, int receiveBufferSize,
                                        int maxCommandSize) throws IOException;

    DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
                                     boolean multicastLoopback, int sendBufferSize) throws IOException;
}
