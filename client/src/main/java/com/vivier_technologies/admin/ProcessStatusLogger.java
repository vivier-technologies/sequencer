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

import com.vivier_technologies.common.admin.MulticastStatusReceiver;
import com.vivier_technologies.common.admin.StatusHandler;
import com.vivier_technologies.common.mux.Multiplexer;
import com.vivier_technologies.common.mux.StandardJVMMultiplexer;
import com.vivier_technologies.utils.*;
import org.apache.commons.configuration2.Configuration;

/**
 * Loop pinging out status requests from all processes listening on multicast channel
 */
public class ProcessStatusLogger implements StatusHandler {

    private final static byte[] _componentName = Logger.generateLoggingKey("PROC_STAT_LOG");

    private final Logger _logger;
    private final Configuration _configuration;
    private final MulticastChannelCreator _channelCreator;
    private final String _ip;
    private final String _multicastAddress;
    private final int _multicastPort;
    private final boolean _multicastLoopback;
    private final int _sendBufferSize;
    private final Multiplexer _mux;
    private final MulticastStatusReceiver _statusReceiver;

    @Override
    public void onEvent(Status status) {
        if(status.getState() == Status.State.PASSIVE) {
            _logger.info(_componentName, "PASSIVE", status.getInstanceName(), status.getMachineName());
        } else {
            _logger.info(_componentName, "ACTIVE", status.getInstanceName(), status.getMachineName());
        }
    }

    public ProcessStatusLogger(Logger logger, Configuration configuration) {
        _logger = logger;
        _configuration = configuration;
        _channelCreator = new MulticastNetworkChannelCreator();

        _ip = configuration.getString("status.receiver.ip");
        _multicastAddress = configuration.getString("status.receiver.multicast.ip");
        _multicastPort = configuration.getInt("status.receiver.multicast.port");
        _multicastLoopback = configuration.getBoolean("loopback");
        _sendBufferSize = configuration.getInt("status.receiver.osbuffersize");

        _mux = new StandardJVMMultiplexer(logger);
        _statusReceiver = new MulticastStatusReceiver(logger, _mux, configuration, _channelCreator);
        _statusReceiver.setHandler(this);
    }

    public void run() throws Exception {
        _mux.open();
        _statusReceiver.open();
        _mux.run();
    }

    public static void main(String[] args) throws Exception {
        Logger logger = new ConsoleLogger();
        ProcessStatusLogger statusLogger = new ProcessStatusLogger(logger, ConfigReader.getConfig(logger, args));
        statusLogger.run();
    }
}
