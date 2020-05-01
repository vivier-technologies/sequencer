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

package com.vivier_technologies.common.admin;

import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastChannelCreator;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class MulticastStatusEmitter implements StatusEmitter {

    private static byte[] _componentName = Logger.generateLoggingKey("MCSTATEMITTER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _maxEventSize;
    private final MulticastChannelCreator _channelCreator;
    private final Logger _logger;

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    @Inject
    public MulticastStatusEmitter(Logger logger, Configuration configuration, MulticastChannelCreator channelCreator) {
        this(logger,
                channelCreator,
                configuration.getString("sequencer.status.emitter.ip"),
                configuration.getString("sequencer.status.emitter.multicast.ip"),
                configuration.getInt("sequencer.status.emitter.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.status.emitter.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));
    }

    public MulticastStatusEmitter(Logger logger, MulticastChannelCreator channelCreator,
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
    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort, _multicastLoopback, _sendBufferSize);
        //_hostName = InetAddress.getByName(_ip).getHostName();
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

    @Override
    public final void sendStatus(boolean isActive) {
        // send a single event packet

        //_channel.send(_buffer, _multicastAddressSocket);
    }

}
