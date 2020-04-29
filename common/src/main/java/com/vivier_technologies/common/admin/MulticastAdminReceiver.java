package com.vivier_technologies.common.admin;

import com.vivier_technologies.admin.ByteBufferCommand;
import com.vivier_technologies.admin.Command;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.MultiplexerHandler;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastUtils;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastAdminReceiver implements AdminReceiver, MultiplexerHandler {

    private static final byte[] _componentName = Logger.generateLoggingKey("ADMINRECEIVER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _receiveBufferSize;
    private final int _maxCommandSize;

    private final Multiplexer _mux;

    private final ByteBufferCommand _adminCommand;

    private final Logger _logger;
    private final ByteBuffer _buffer;

    private DatagramChannel _channel;
    private AdminHandler _listener;

    @Inject
    public MulticastAdminReceiver(Logger logger, Multiplexer mux, Configuration configuration) {

        this(logger,
                mux,
                configuration.getString("sequencer.admin.receiver.ip"),
                configuration.getString("sequencer.admin.receiver.multicast.ip"),
                configuration.getInt("sequencer.admin.receiver.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.admin.receiver.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));

    }

    public MulticastAdminReceiver(Logger logger, Multiplexer mux,
                                  String ip, String multicastAddress, int multicastPort,
                                  boolean multicastLoopback, int receiveBufferSize, int maxCommandSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _receiveBufferSize = receiveBufferSize;
        _maxCommandSize = maxCommandSize;

        _logger = logger;
        _mux = mux;

        //TODO consider whether to allocate direct or not here..
        _buffer = ByteBufferFactory.nativeAllocateDirect(_maxCommandSize);

        _adminCommand = new ByteBufferCommand();
    }

    @Override
    public final void open() throws IOException {
        _channel = MulticastUtils.setupReceiveChannel(_ip, _multicastAddress, _multicastPort, _multicastLoopback,
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
            // need to check whether this message is for me i.e. instance matches
            _adminCommand.setData(_buffer);
            switch(_adminCommand.getType()) {
                case Command.Type.GO_ACTIVE -> _listener.onGoActive();
                case Command.Type.GO_PASSIVE -> _listener.onGoPassive();
                case Command.Type.STATUS -> _listener.onStatusRequest();
            }
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to read from channel into buffer");
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
            _logger.error(_componentName, "Unable to close socket");
        }
    }

    @Override
    public void setHandler(AdminHandler listener) {
        _listener = listener;
    }
}
