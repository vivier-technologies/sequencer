package com.vivier_technologies.sequencer.emitter;


import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastChannelCreator;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MulticastEventEmitter implements EventEmitter {
    private static byte[] _componentName = Logger.generateLoggingKey("MCEVTEMITTER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _maxEventSize;

    private final Logger _logger;
    private final MulticastChannelCreator _channelCreator;

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    @Inject
    public MulticastEventEmitter(Logger logger, Configuration configuration,
                                 MulticastChannelCreator channelCreator) throws IOException {
        this(logger,
                channelCreator,
                configuration.getString("sequencer.event.emitter.ip"),
                configuration.getString("sequencer.event.emitter.multicast.ip"),
                configuration.getInt("sequencer.event.emitter.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.event.emitter.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));
    }

    public MulticastEventEmitter(Logger logger, MulticastChannelCreator channelCreator,
                                 String ip, String multicastAddress, int multicastPort,
                                 boolean multicastLoopback, int sendBufferSize, int maxEventSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
        _maxEventSize = maxEventSize;
        _logger = logger;
        _channelCreator = channelCreator;
    }

    @Override
    public final void send(Event event) throws IOException {
        // send a single event packet
        ByteBuffer buffer = event.getData();
        if(buffer.limit() > _maxEventSize) {
            _logger.warn(_componentName, "Attempting to send event that is larger than maxmessagesize");
        }
        _channel.send(buffer, _multicastAddressSocket);
    }

    @Override
    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort, _multicastLoopback, _sendBufferSize);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    @Override
    public final void close() {
        try {
            if(_channel != null) {
                _channel.close();
            }
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to close event emitter");
        }
    }
}
