package sequencer.events;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface EventEmitter {

    boolean send(ByteBuffer event) throws IOException;

}
