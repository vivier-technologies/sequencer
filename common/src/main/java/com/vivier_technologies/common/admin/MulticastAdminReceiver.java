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

import com.vivier_technologies.admin.ByteBufferCommand;
import com.vivier_technologies.admin.Command;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.MultiplexerHandler;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastChannelCreator;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class MulticastAdminReceiver implements AdminReceiver, MultiplexerHandler {

    private static final byte[] _loggingKey = Logger.generateLoggingKey("ADMINRECEIVER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _receiveBufferSize;
    private final int _maxCommandSize;

    private final Multiplexer _mux;
    private final MulticastChannelCreator _channelCreator;
    private final ByteBufferCommand _adminCommand;

    private final Logger _logger;
    private final ByteBuffer _buffer;

    private DatagramChannel _channel;
    private AdminHandler _listener;

    private final byte[] _instanceName;

    @Inject
    public MulticastAdminReceiver(Logger logger, Multiplexer mux, Configuration configuration,
                                  MulticastChannelCreator channelCreator) {

        this(logger,
                mux,
                channelCreator,
                configuration.getString("instance"),
                configuration.getString("admin.receiver.ip"),
                configuration.getString("admin.receiver.multicast.ip"),
                configuration.getInt("admin.receiver.multicast.port"),
                configuration.getBoolean("loopback"),
                configuration.getInt("admin.receiver.osbuffersize"),
                configuration.getInt("maxmessagesize"));

    }

    public MulticastAdminReceiver(Logger logger, Multiplexer mux, MulticastChannelCreator channelCreator,
                                  String instance, String ip, String multicastAddress, int multicastPort,
                                  boolean multicastLoopback, int receiveBufferSize, int maxCommandSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _receiveBufferSize = receiveBufferSize;
        _maxCommandSize = maxCommandSize;

        _logger = logger;
        _mux = mux;
        _channelCreator = channelCreator;

        _buffer = ByteBufferFactory.nativeAllocateDirect(_maxCommandSize);

        _adminCommand = new ByteBufferCommand();

        _instanceName = Command.validateInstanceName(instance);
    }

    @Override
    public final void open() throws IOException {
        _channel = _channelCreator.setupReceiveChannel(_ip, _multicastAddress, _multicastPort, _multicastLoopback,
                _receiveBufferSize, _maxCommandSize);

        _mux.register(_channel, SelectionKey.OP_READ, this);
    }

    @Override
    public final void onConnect() {

    }

    @Override
    public final void onAccept() {

    }

    @Override
    public final void onRead() {
        try {
            // will return a single datagram or nothing
            _buffer.clear();
            _channel.receive(_buffer);
            _buffer.flip();

            _adminCommand.setData(_buffer);
            byte[] instance = _adminCommand.getInstance();
            if (Arrays.equals(instance, _instanceName) || Arrays.equals(instance, Command.ALL_INSTANCES)) {
                switch(_adminCommand.getType()) {
                    case Command.Type.GO_ACTIVE -> _listener.onGoActive();
                    case Command.Type.GO_PASSIVE -> _listener.onGoPassive();
                    case Command.Type.STATUS -> _listener.onStatusRequest();
                    case Command.Type.SHUTDOWN -> _listener.onShutdown();
                }
            }
        } catch (IOException e) {
            _logger.error(_loggingKey, "Unable to read from channel into buffer");
        }
    }

    @Override
    public final void onWrite() {

    }

    public final void close() {
        try {
            if(_channel != null) {
                _mux.remove(_channel);
                _channel.close();
            }
        } catch (IOException e) {
            _logger.error(_loggingKey, "Unable to close socket");
        }
    }

    @Override
    public void setHandler(AdminHandler listener) {
        _listener = listener;
    }
}
