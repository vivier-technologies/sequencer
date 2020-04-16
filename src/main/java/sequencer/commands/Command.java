package sequencer.commands;

import java.nio.ByteBuffer;

public interface Command {

    int CMD_BODY_START = CommandHeader.CMD_HEADER_LEN;

    CommandHeader getHeader();

    ByteBuffer getData();

}
