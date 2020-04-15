package sequencer.events;

import java.io.IOException;

public class TestEventEmitter implements EventEmitter {

    @Override
    public boolean send(Event event) throws IOException {
        return false;
    }

    @Override
    public void open() throws IOException {

    }
}
