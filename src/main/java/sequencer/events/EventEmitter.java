package sequencer.events;

import java.io.IOException;

public interface EventEmitter {

    boolean send(Event event) throws IOException;

}
