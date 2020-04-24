package com.vivier_technologies;

import com.vivier_technologies.commands.ByteBufferCommand;
import com.vivier_technologies.commands.Command;
import com.vivier_technologies.utils.ByteBufferFactory;
import com.vivier_technologies.utils.ConfigReader;
import com.vivier_technologies.utils.ConsoleLogger;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class BasicCommandSender {
    public static byte[] _componentName = Logger.generateLoggingKey("CMDSENDER");

    private final String _multicastAddress;
    private final String _ip;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final int _maxCommandSize;

    private Logger _logger;
    private DatagramChannel _channel;
    private SocketAddress _multicastAddressSocket;

    public BasicCommandSender(Logger logger, Configuration configuration) throws IOException {
        this(logger,
                configuration.getString("sequencer.commandsender.ip"),
                configuration.getString("sequencer.commandsender.multicast.ip"),
                configuration.getInt("sequencer.commandsender.multicast.port"),
                configuration.getBoolean("sequencer.loopback"),
                configuration.getInt("sequencer.commandsender.osbuffersize"),
                configuration.getInt("sequencer.maxmessagesize"));
    }

    public BasicCommandSender(Logger logger, String ip, String multicastAddress, int multicastPort,
                                 boolean multicastLoopback, int sendBufferSize, int maxCommandSize) {
        _ip = ip;
        _multicastAddress = multicastAddress;
        _multicastPort = multicastPort;
        _multicastLoopback = multicastLoopback;
        _sendBufferSize = sendBufferSize;
        _maxCommandSize = maxCommandSize;
        _logger = logger;
    }

    public final boolean send(Command command) throws IOException {
        // send a single event packet
        ByteBuffer buffer = command.getData();
        if(buffer.limit() > _maxCommandSize) {
            _logger.warn(_componentName, "Attempting to send command that is larger than maxmessagesize");
        }
        return _channel.send(buffer, _multicastAddressSocket) != 0;
    }

    public final void open() throws IOException {
        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        _channel.configureBlocking(false);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(_ip));
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, _multicastLoopback);
        _channel.setOption(StandardSocketOptions.SO_SNDBUF, _sendBufferSize);
        _channel.bind(null); // select any local address
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel.join(multicastAddress, nif);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    public final void close() {
        try {
            _channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Logger logger = new ConsoleLogger();
        BasicCommandSender sender = new BasicCommandSender(logger, ConfigReader.getConfig(logger, args));
        sender.open();
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(1024);
        ByteBufferCommand command = new ByteBufferCommand(buffer);
        byte[] body = "TESTTESTTEST".getBytes();
        buffer.put(Command.CMD_BODY_START, body);
        command.getHeader().setHeader(Command.CMD_BODY_START + body.length, (short)1, "SENDER12".getBytes(), 1);
        buffer.position(Command.CMD_BODY_START + body.length);
        buffer.flip();
        if(sender.send(command)) {
            logger.info(sender._componentName, "SENT");
        }
        sender.close();
    }
}
