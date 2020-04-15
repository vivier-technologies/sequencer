package sequencer.commands;

import java.io.IOException;

public interface CommandReceiver {

    void open() throws IOException;
}
