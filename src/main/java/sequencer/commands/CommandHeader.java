package sequencer.commands;

public interface CommandHeader {

    int CMD_LEN = 0;
    int TYPE = 4;
    int SRC = 6;
    int SRC_LEN = 8;
    int CMD_SEQ = SRC + SRC_LEN;

    int CMD_HEADER_LEN = CMD_SEQ + 4;

    int getLength();

    short getType();

    byte[] getSource();

    int getSequence();

}
