package sequencer.events;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class EventEmitterImpl implements EventEmitter {
        private DatagramChannel _channel;
        private SocketAddress _multicastAddress;

    public EventEmitterImpl(String ip, String multicastAddress, int multicastPort) throws IOException {
        _channel = DatagramChannel.open(StandardProtocolFamily.INET);
        _channel.setOption(StandardSocketOptions.IP_MULTICAST_IF,
                NetworkInterface.getByInetAddress(InetAddress.getByName(ip)));
        _channel.bind(null); // select any local address
        _multicastAddress = new InetSocketAddress(multicastAddress, multicastPort);
    }

    @Override
    public boolean send(ByteBuffer event) throws IOException {
        return _channel.send(event, _multicastAddress) != 0;
    }
}
