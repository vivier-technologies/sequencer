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

package com.vivier_technologies.admin;

import com.vivier_technologies.utils.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Send an admin request to a process or processes (using * as a wildcard)
 */
public class ProcessController {

    private final static byte[] _componentName = Logger.generateLoggingKey("PROC_CTRL");

    private final Logger _logger;
    private final Configuration _configuration;
    private final MulticastChannelCreator _channelCreator;
    private final String _ip;
    private final String _multicastAddress;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final ByteBuffer _buffer;

    private DatagramChannel _channel;
    private InetSocketAddress _multicastAddressSocket;

    public ProcessController(Logger logger, Configuration configuration) {
        _logger = logger;
        _configuration = configuration;
        _channelCreator = new MulticastNetworkChannelCreator();

        _ip = configuration.getString("admin.sender.ip");
        _multicastAddress = configuration.getString("admin.sender.multicast.ip");
        _multicastPort = configuration.getInt("admin.sender.multicast.port");
        _multicastLoopback = configuration.getBoolean("loopback");
        _sendBufferSize = configuration.getInt("admin.sender.osbuffersize");

        _buffer = ByteBufferFactory.nativeAllocate(1024);
    }

    public final void open() throws IOException {
        InetAddress multicastAddress = InetAddress.getByName(_multicastAddress);
        _channel = _channelCreator.setupSendChannel(_ip, multicastAddress, _multicastPort, _multicastLoopback, _sendBufferSize);
        _multicastAddressSocket = new InetSocketAddress(multicastAddress, _multicastPort);
    }

    public final void sendActive(String instance) throws IOException {
        _logger.info(_componentName, "Sending goactive");
        send(Command.Type.GO_ACTIVE, instance);
    }

    public final void sendPassive(String instance) throws IOException {
        _logger.info(_componentName, "Sending gopassive");
        send(Command.Type.GO_PASSIVE, instance);
    }

    public final void sendShutdown(String instance) throws IOException {
        _logger.info(_componentName, "Sending shutdown");
        send(Command.Type.SHUTDOWN, instance);
    }

    public final void sendStatusUpdateRequest(String instance) throws IOException {
        _logger.info(_componentName, "Sending status update request");
        send(Command.Type.STATUS, instance);
    }

    private void send(short type, String instance) throws IOException {
        byte[] instanceName = Command.validateInstanceName(instance);
        _buffer.clear();
        _buffer.putShort(Command.TYPE, type);
        _buffer.put(Command.INSTANCE_NAME, instanceName, 0, instanceName.length);
        _buffer.position(Command.ADMIN_CMD_LEN);
        _buffer.flip();

        _channel.send(_buffer, _multicastAddressSocket);

        _logger.info(_componentName, "Sent command to ", instance);
    }

    public final void close() throws IOException {
        _channel.close();
    }

    public static void main(String[] args) throws Exception {
        Logger logger = new ConsoleLogger();
        Options options = new Options();
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("commandtype")
                .hasArg()
                .withDescription("The command type to send - goactive, gopassive, shutdown, status")
                .isRequired()
                .create("commandtype"));
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("instancename")
                .hasArg()
                .withDescription("The instance to send the command to or * for all")
                .isRequired()
                .create("instancename"));
        CommandLine commandLine = ConfigReader.getCommandLine(options, logger, args);
        Configuration config = ConfigReader.getConfig(commandLine, logger, args);
        ProcessController processController = new ProcessController(logger, config);
        processController.open();

        switch(commandLine.getOptionValue("commandtype")) {
            case "goactive" -> processController.sendActive(commandLine.getOptionValue("instancename"));
            case "gopassive" -> processController.sendPassive(commandLine.getOptionValue("instancename"));
            case "shutdown" -> processController.sendShutdown(commandLine.getOptionValue("instancename"));
            case "status" -> processController.sendStatusUpdateRequest(commandLine.getOptionValue("instancename"));
        }

        processController.close();
    }
}
