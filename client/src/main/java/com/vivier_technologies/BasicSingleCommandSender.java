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

package com.vivier_technologies;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.commands.Command;
import com.vivier_technologies.commands.CommandHeader;
import com.vivier_technologies.common.eventreceiver.EventHandler;
import com.vivier_technologies.common.eventreceiver.MulticastEventReceiver;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.StandardJVMMultiplexer;
import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.*;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class BasicSingleCommandSender implements EventHandler {
    public static byte[] _componentName = Logger.generateLoggingKey("CMDSENDER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _maxCommandSize;
    private final byte[] _source;
    private final MulticastEventReceiver _eventReceiver;
    private final Multiplexer _mux;
    private final ByteBuffer _buffer;
    private final ByteBufferCommand _command;
    private final Logger _logger;
    private final MulticastChannelCreator _channelCreator;
    private final int _ttl;

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    public BasicSingleCommandSender(Logger logger, Configuration configuration) throws IOException {
        _source = CommandHeader.validateSource(configuration.getString("source"));
        _ip = configuration.getString("command.sender.ip");
        _multicastAddress = configuration.getString("command.sender.multicast.ip");
        _multicastPort = configuration.getInt("command.sender.multicast.port");
        _multicastLoopback = configuration.getBoolean("loopback");
        _sendBufferSize = configuration.getInt("command.sender.osbuffersize");
        _maxCommandSize = configuration.getInt("maxmessagesize");
        _ttl = configuration.getInt("ttl");
        _logger = logger;

        _mux = new StandardJVMMultiplexer(logger);
        _channelCreator = new MulticastNetworkChannelCreator();
        _eventReceiver = new MulticastEventReceiver(logger, _mux, configuration, _channelCreator);
        _eventReceiver.setHandler(this);

        _buffer = ByteBufferFactory.nativeAllocate(_maxCommandSize);
        _command = new ByteBufferCommand(_buffer);
    }


    public final void send(int cmdSeq, short type, byte[] body) throws IOException {
        _buffer.clear();
        _buffer.put(Command.CMD_BODY_START, body);
        _command.getHeader().setHeader(Command.CMD_BODY_START + body.length, type, _source, cmdSeq);
        _buffer.position(Command.CMD_BODY_START + body.length);
        _buffer.flip();

        if(_buffer.limit() > _maxCommandSize) {
            _logger.warn(_componentName, "Attempting to send command that is larger than maxmessagesize");
        }
        _logger.info(_componentName, "SENDING");
        _channel.send(_buffer, _multicastAddressSocket);
    }

    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort,
                _multicastLoopback, _sendBufferSize, _ttl);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);

        _mux.open();
        _eventReceiver.open();
    }

    public final void waitForEvent() throws IOException {
        // bit of a hack - should use mux but will suffice for now..
        _mux.run();
    }

    @Override
    public void onEvent(Event event) {
        // validate it was ours - can just check source because we only send one at a time
        if(Arrays.equals(event.getHeader().getSource(), _source)) {
            _logger.info(_componentName, "RCVD");
            close();
        }
    }

    public final void close() {
        try {
            _channel.close();
            _eventReceiver.close();
            _mux.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Logger logger = new ConsoleLogger();
        BasicSingleCommandSender sender = new BasicSingleCommandSender(logger, ConfigReader.getConfig(logger, args));
        sender.open();

        sender.send(1, (short)1, "TESTTESTTEST".getBytes());

        sender.waitForEvent();
    }
}
