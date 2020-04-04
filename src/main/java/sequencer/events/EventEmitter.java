package sequencer.events;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface EventEmitter {

    boolean send(Event event) throws IOException;

}
