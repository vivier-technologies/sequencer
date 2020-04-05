package sequencer.commands;

import java.nio.ByteBuffer;

public interface Command {

    CommandHeader getHeader();

    ByteBuffer getData();

}
