package sequencer.commands;

import org.apache.commons.configuration2.Configuration;
import sequencer.processor.CommandProcessor;
import sequencer.utils.Logger;
import sequencer.utils.Multiplexer;
import sequencer.utils.MultiplexerListener;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastCommandReceiver implements CommandReceiver, MultiplexerListener {
    private static byte[] _componentName = "CMDRECEIVER".getBytes();

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _receiveBufferSize;
    private final Multiplexer _mux;
    private final CommandProcessor _processor;
    private final ByteBufferCommand _command;

    private Logger _logger;
    private DatagramChannel _channel;

    private ByteBuffer _buffer;

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
                configuration.getInt("sequencer.commandreceiver.buffersize"));

    }

    public MulticastCommandReceiver(Logger logger, Multiplexer mux, CommandProcessor processor,
                                    String ip, String multicastAddress, int multicastPort,
                                    boolean multicastLoopback, int receiveBufferSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _receiveBufferSize = receiveBufferSize;

        _logger = logger;
        _mux = mux;
        _processor = processor;

        //TODO consider whether to allocate direct or not here..
        _buffer = ByteBuffer.allocateDirect(receiveBufferSize);

        //TODO put into factory - if so need write interface to command as well
        _command = new ByteBufferCommand();
    }

    @Override
    public void open() throws IOException {
        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        _channel.configureBlocking(false);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(_ip));
        _channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, _multicastLoopback);
        _channel.setOption(StandardSocketOptions.SO_RCVBUF, _receiveBufferSize);

        _channel.bind(new InetSocketAddress(_multicastPort));
        _channel.join(InetAddress.getByName(_multicastAddress), nif);

        _mux.register(_channel, SelectionKey.OP_READ, this);
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onAccept() {

    }

    @Override
    public void onRead() {
        // safe as command sender will timeout
        try {
            if(_channel.read(_buffer) > 0) {
                _command.setData(_buffer);
                _processor.process(_command);
            } else {
                _logger.warn(_componentName, "Nothing to read when called back - investigate");
            }
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to read from channel into buffer");
        }

    }

    @Override
    public void onWrite() {

    }

    @Override
    public void onShutdown() {
        try {
            _channel.close();
        } catch (IOException e) {
            _logger.error(_componentName, "Unable to close socket");
        }
    }

}
