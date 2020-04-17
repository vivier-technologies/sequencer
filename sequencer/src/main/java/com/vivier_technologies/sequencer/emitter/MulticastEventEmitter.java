package com.vivier_technologies.sequencer.emitter;


import com.vivier_technologies.events.Event;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

public class MulticastEventEmitter implements EventEmitter {
    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;
    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;

    @Inject
    public MulticastEventEmitter(Configuration configuration) throws IOException {
        this(configuration.getString("sequencer.eventemitter.ip"),
                configuration.getString("sequencer.eventemitter.multicast.ip"),
                configuration.getInt("sequencer.eventemitter.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.eventemitter.buffersize"));
    }

    public MulticastEventEmitter(String ip, String multicastAddress, int multicastPort,
                                 boolean multicastLoopback, int sendBufferSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
    }

    @Override
    public boolean send(Event event) throws IOException {
        return _channel.send(event.getData(), _multicastAddressSocket) != 0;
    }

    @Override
    public void open() throws IOException {
        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        _channel.configureBlocking(false);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(_ip));
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, _multicastLoopback);
        _channel.setOption(StandardSocketOptions.SO_SNDBUF, _sendBufferSize);
        _channel.bind(null); // select any local address
        _multicastAddressSocket = new InetSocketAddress(_multicastAddress, _multicastPort);
    }
}
