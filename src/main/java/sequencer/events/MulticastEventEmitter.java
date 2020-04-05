package sequencer.events;


import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

public class MulticastEventEmitter implements EventEmitter {
    private DatagramChannel _channel;
    private SocketAddress _multicastAddress;

    @Inject
    public MulticastEventEmitter(Configuration configuration) throws IOException {
        //TODO implement properly
        this(configuration.getString("sequencer.eventemitter.ip"),
                configuration.getString("sequencer.eventemitter.multicast.ip"),
                configuration.getInt("sequencer.eventemitter.multicast.port"),
                        configuration.getBoolean("sequencer.loopback"));
    }

    public MulticastEventEmitter(String ip, String multicastAddress, int multicastPort, boolean multicastLoopback) throws IOException {
        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, multicastLoopback);
        _channel.bind(null); // select any local address
        _multicastAddress = new InetSocketAddress(multicastAddress, multicastPort);
    }

    @Override
    public boolean send(Event event) throws IOException {
        return _channel.send(event.getData(), _multicastAddress) != 0;
    }
}
