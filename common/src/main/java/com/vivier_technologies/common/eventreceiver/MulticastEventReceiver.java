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

package com.vivier_technologies.common.eventreceiver;

import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.MultiplexerHandler;
import com.vivier_technologies.events.ByteBufferEvent;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastChannelCreator;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastEventReceiver implements EventReceiver, MultiplexerHandler {

    private static final byte[] _loggingKey = Logger.generateLoggingKey("EVTRECEIVER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _receiveBufferSize;
    private final int _maxEventSize;

    private final Multiplexer _mux;
    private final ByteBufferEvent _event;
    private final Logger _logger;
    private final ByteBuffer _buffer;
    private final MulticastChannelCreator _channelCreator;

    private DatagramChannel _channel;
    private EventHandler _listener;

    @Inject
    public MulticastEventReceiver(Logger logger, Multiplexer mux, Configuration configuration,
                                  MulticastChannelCreator channelCreator) {

        this(logger,
                mux,
                channelCreator,
                configuration.getString("event.receiver.ip"),
                configuration.getString("event.receiver.multicast.ip"),
                configuration.getInt("event.receiver.multicast.port"),
                configuration.getBoolean("loopback"),
                configuration.getInt("event.receiver.osbuffersize"),
                configuration.getInt("maxmessagesize"));

    }

    public MulticastEventReceiver(Logger logger, Multiplexer mux, MulticastChannelCreator channelCreator,
                                    String ip, String multicastAddress, int multicastPort,
                                    boolean multicastLoopback, int receiveBufferSize, int maxEventSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _receiveBufferSize = receiveBufferSize;
        _maxEventSize = maxEventSize;

        _logger = logger;
        _mux = mux;
        _channelCreator = channelCreator;

        //TODO consider whether to allocate direct or not here..
        _buffer = ByteBufferFactory.nativeAllocateDirect(_maxEventSize);

        _event = new ByteBufferEvent();
    }

    @Override
    public void setHandler(EventHandler listener) {
        _listener = listener;
    }

    @Override
    public final void open() throws IOException {
        _channel = _channelCreator.setupReceiveChannel(_ip, _multicastAddress, _multicastPort, _multicastLoopback,
                _receiveBufferSize, _maxEventSize);

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
            _event.setData(_buffer);
            // deliberately missing a check for whether the listener is set given this is on the critical
            // processing path...
            _listener.onEvent(_event);
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
}
