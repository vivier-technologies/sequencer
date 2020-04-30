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
                                               boolean multicastLoopback, int receiveBufferSize, int maxCommandSize) throws IOException {
        return _receiveChannel;
    }

    @Override
    public DatagramChannel setupSendChannel(String ip, InetAddress multicastAddress, int multicastPort,
                                            boolean multicastLoopback, int sendBufferSize) throws IOException {
        return _sendChannel;
    }
}