package com.vivier_technologies.sequencer.emitter;


import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastUtils;
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

    private Logger _logger;
    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    @Inject
    public MulticastEventEmitter(Logger logger, Configuration configuration) throws IOException {
        this(logger,
                configuration.getString("sequencer.eventemitter.ip"),
                configuration.getString("sequencer.eventemitter.multicast.ip"),
                configuration.getInt("sequencer.eventemitter.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.eventemitter.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));
    }

    public MulticastEventEmitter(Logger logger, String ip, String multicastAddress, int multicastPort,
                                 boolean multicastLoopback, int sendBufferSize, int maxEventSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
        _maxEventSize = maxEventSize;
        _logger = logger;
    }

    @Override
    public final boolean send(Event event) throws IOException {
        // send a single event packet
        ByteBuffer buffer = event.getData();
        if(buffer.limit() > _maxEventSize) {
            _logger.warn(_componentName, "Attempting to send event that is larger than maxmessagesize");
        }
        return _channel.send(buffer, _multicastAddressSocket) != 0;
    }

    @Override
    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = MulticastUtils.setupSendChannel(_ip, multicastAddress, _multicastPort, _multicastLoopback, _sendBufferSize);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    @Override
    public final void close() {
        try {
            _channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
