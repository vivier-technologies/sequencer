package sequencer.commands;

public interface CommandHeader {

    int getLength();

    short getType();

    byte[] getSource();

    int getSequence();

}
