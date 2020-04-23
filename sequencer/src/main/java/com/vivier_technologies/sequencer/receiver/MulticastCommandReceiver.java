package com.vivier_technologies.sequencer.receiver;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.sequencer.processor.CommandProcessor;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.Logger;
import com.vivier_technologies.utils.Multiplexer;
import com.vivier_technologies.utils.MultiplexerListener;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
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
    private final CommandProcessor _processor;
    private final ByteBufferCommand _command;

    private final Logger _logger;

    private DatagramChannel _channel;
    private final ByteBuffer _buffer;

    private final int NETWORK_HEADERS = 8+20; // UDP and IP respectively

    @Inject
    public MulticastCommandReceiver(Logger logger, Multiplexer mux, Configuration configuration,
                                    CommandProcessor processor) {

        this(logger,
                mux,
                processor,
                configuration.getString("sequencer.commandreceiver.ip"),
                configuration.getString("sequencer.commandreceiver.multicast.ip"),
                configuration.getInt("sequencer.commandreceiver.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.commandreceiver.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));

    }

    public MulticastCommandReceiver(Logger logger, Multiplexer mux, CommandProcessor processor,
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
        _processor = processor;

        //TODO consider whether to allocate direct or not here..
        _buffer = ByteBufferFactory.nativeAllocateDirect(_maxCommandSize);

        _command = new ByteBufferCommand();
    }

    @Override
    public final void open() throws IOException {
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(_ip));
        if(_maxCommandSize > nif.getMTU() - NETWORK_HEADERS)
            throw new IllegalArgumentException("Command receiver buffer size too large");

        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        _channel.configureBlocking(false);


        _channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, _multicastLoopback);
        _channel.setOption(StandardSocketOptions.SO_RCVBUF, _receiveBufferSize);

        _channel.bind(new InetSocketAddress(_multicastPort));
        _channel.join(InetAddress.getByName(_multicastAddress), nif);

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
            _channel.receive(_buffer);
            _command.setData(_buffer);
            _processor.process(_command);
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
