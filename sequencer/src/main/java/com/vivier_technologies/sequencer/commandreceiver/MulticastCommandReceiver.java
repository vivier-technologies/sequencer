package com.vivier_technologies.sequencer.commandreceiver;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.MultiplexerListener;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.MulticastUtils;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastCommandReceiver implements CommandReceiver, MultiplexerListener {
    private static final byte[] _componentName = Logger.generateLoggingKey("CMDRECEIVER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _receiveBufferSize;
    private final int _maxCommandSize;

    private final Multiplexer _mux;
    private final ByteBufferCommand _command;

    private final Logger _logger;
    private final ByteBuffer _buffer;

    private DatagramChannel _channel;
    private CommandListener _listener;

    @Inject
    public MulticastCommandReceiver(Logger logger, Multiplexer mux, Configuration configuration) {

        this(logger,
                mux,
                configuration.getString("sequencer.commandreceiver.ip"),
                configuration.getString("sequencer.commandreceiver.multicast.ip"),
                configuration.getInt("sequencer.commandreceiver.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.commandreceiver.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));

    }

    public MulticastCommandReceiver(Logger logger, Multiplexer mux,
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

        _command = new ByteBufferCommand();
    }

    @Override
    public void setListener(CommandListener listener) {
        _listener = listener;
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
            _command.setData(_buffer);
            // deliberately missing a check for whether the listener is set given this is on the critical
            // processing path...
            _listener.onCommand(_command);
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to read from channel into buffer");
        }

    }

    @Override
    public final void onWrite() {

    }

    public final void close() {
        try {
            _mux.remove(_channel);
            _channel.close();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to close socket");
        }
    }

}
