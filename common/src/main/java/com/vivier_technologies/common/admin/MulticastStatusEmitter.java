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

import com.vivier_technologies.admin.Command;
import com.vivier_technologies.admin.Status;
import com.vivier_technologies.utils.ByteArrayUtils;
import com.vivier_technologies.utils.ByteBufferFactory;
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

public class MulticastStatusEmitter implements StatusEmitter {

    private static byte[] _componentName = Logger.generateLoggingKey("MCSTATEMITTER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _ttl;
    private final MulticastChannelCreator _channelCreator;
    private final Logger _logger;
    private final ByteBuffer _buffer;
    private final byte[] _machineName = new byte[Status.MACHINE_NAME_LEN];

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    @Inject
    public MulticastStatusEmitter(Logger logger, Configuration configuration, MulticastChannelCreator channelCreator) {
        this(logger,
                channelCreator,
                configuration.getString("instance"),
                configuration.getString("status.emitter.ip"),
                configuration.getString("status.emitter.multicast.ip"),
                configuration.getInt("status.emitter.multicast.port"),
                configuration.getBoolean("loopback"),
                configuration.getInt("status.emitter.osbuffersize"),
                configuration.getInt("ttl"));
    }

    public MulticastStatusEmitter(Logger logger, MulticastChannelCreator channelCreator, String instanceName,
                                  String ip, String multicastAddress, int multicastPort,
                                  boolean multicastLoopback, int sendBufferSize, int ttl) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
        _logger = logger;
        _channelCreator = channelCreator;
        _ttl = ttl;

        _buffer = ByteBufferFactory.nativeAllocateDirect(Status.STATUS_LEN);
        _buffer.put(Status.INSTANCE_NAME, Command.validateInstanceName(instanceName));
    }

    @Override
    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort,
                _multicastLoopback, _sendBufferSize, _ttl);
        ByteArrayUtils.copyAndTruncateOrPadRightWithSpaces(
                InetAddress.getByName(_ip).getHostName().getBytes(), _machineName, 0, Status.MACHINE_NAME_LEN);
        _buffer.put(Status.MACHINE_NAME, _machineName);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    @Override
    public final void close() {
        try {
            if(_channel != null) {
                _channel.close();
            }
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to close status emitter");
        }
    }

    @Override
    public final void sendStatus(boolean isActive) {
        // set the status - the rest of the status event is preset
        if (isActive) {
            _buffer.put(Status.STATE, Status.State.ACTIVE);
        } else {
            _buffer.put(Status.STATE, Status.State.PASSIVE);
        }
        _buffer.position(Status.STATUS_LEN).flip();

        try {
            _channel.send(_buffer, _multicastAddressSocket);
        } catch (IOException e) {
            _logger.info(_componentName, "Unable to send out status");
        }
    }

}
