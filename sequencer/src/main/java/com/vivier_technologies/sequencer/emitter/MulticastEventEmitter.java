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
    private static byte[] _loggingKey = Logger.generateLoggingKey("MCEVTEMITTER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _maxEventSize;
    private final int _ttl;

    private final Logger _logger;
    private final MulticastChannelCreator _channelCreator;

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    @Inject
    public MulticastEventEmitter(Logger logger, Configuration configuration,
                                 MulticastChannelCreator channelCreator) throws IOException {
        this(logger,
                channelCreator,
                configuration.getString("event.emitter.ip"),
                configuration.getString("event.emitter.multicast.ip"),
                configuration.getInt("event.emitter.multicast.port"),
                configuration.getBoolean("loopback"),
                configuration.getInt("event.emitter.osbuffersize"),
                configuration.getInt("maxmessagesize"),
                configuration.getInt("ttl"));
    }

    public MulticastEventEmitter(Logger logger, MulticastChannelCreator channelCreator,
                                 String ip, String multicastAddress, int multicastPort,
                                 boolean multicastLoopback, int sendBufferSize, int maxEventSize, int ttl) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
        _maxEventSize = maxEventSize;
        _logger = logger;
        _channelCreator = channelCreator;
        _ttl = ttl;
    }

    @Override
    public final void send(Event event) throws IOException {
        // send a single event packet
        ByteBuffer buffer = event.getData();
        if(buffer.limit() > _maxEventSize) {
            _logger.error(_loggingKey, "Attempting to send event that is larger than maxmessagesize");
        }
        // TODO in theory this could block but assuming the os buffer size is large then it shouldn't
        if(_channel.send(buffer, _multicastAddressSocket) == 0) {
            _logger.error(_loggingKey, "Event wasn't sent out");
        }
    }

    @Override
    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort,
                _multicastLoopback, _sendBufferSize, _ttl);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    @Override
    public final void close() {
        try {
            if(_channel != null) {
                _channel.close();
            }
        } catch (IOException e) {
            _logger.error(_loggingKey, "Unable to close event emitter");
        }
    }
}
