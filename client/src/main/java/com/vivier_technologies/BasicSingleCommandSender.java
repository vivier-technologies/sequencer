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
    private final byte[] _source = new byte[CommandHeader.SRC_LEN];
    private final MulticastEventReceiver _eventReceiver;
    private final Multiplexer _mux;
    private final ByteBuffer _buffer;
    private final ByteBufferCommand _command;
    private final Logger _logger;
    private final MulticastChannelCreator _channelCreator;

    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    public BasicSingleCommandSender(Logger logger, Configuration configuration) throws IOException {
        String source = configuration.getString("source");
        ByteArrayUtils.copyAndPadRightWithSpaces(source.getBytes(), _source, 0, _source.length);
        _ip = configuration.getString("sequencer.command.sender.ip");
        _multicastAddress = configuration.getString("sequencer.command.sender.multicast.ip");
        _multicastPort = configuration.getInt("sequencer.command.sender.multicast.port");
        _multicastLoopback = configuration.getBoolean("sequencer.loopback");
        _sendBufferSize = configuration.getInt("sequencer.command.sender.osbuffersize");
        _maxCommandSize = configuration.getInt("sequencer.maxmessagesize");
        _logger = logger;

        _mux = new StandardJVMMultiplexer(logger);
        _channelCreator = new MulticastNetworkChannelCreator();
        _eventReceiver = new MulticastEventReceiver(logger, _mux, configuration, _channelCreator);
        _eventReceiver.setHandler(this);

        _buffer = ByteBufferFactory.nativeAllocate(1024);
        _command = new ByteBufferCommand(_buffer);
    }


    public final boolean send(int cmdSeq, short type, byte[] body) throws IOException {
        _buffer.clear();
        _buffer.put(Command.CMD_BODY_START, body);
        _command.getHeader().setHeader(Command.CMD_BODY_START + body.length, type, _source, cmdSeq);
        _buffer.position(Command.CMD_BODY_START + body.length);
        _buffer.flip();

        if(_buffer.limit() > _maxCommandSize) {
            _logger.warn(_componentName, "Attempting to send command that is larger than maxmessagesize");
        }
        return _channel.send(_buffer, _multicastAddressSocket) != 0;
    }

    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort, _multicastLoopback, _sendBufferSize);
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

        if(sender.send(1, (short)1, "TESTTESTTEST".getBytes())) {
            logger.info(BasicSingleCommandSender._componentName, "SENT");
        }
        sender.waitForEvent();
    }
}
