package sequencer.utils;

/**
 * Slightly silly interface but intention to write an object efficient logger behind this eventually
 * that can push out to variety of locations - file, indexing service etc
 */
public interface Logger {

    void log(byte[] component, String s);

    void log(byte[] component, String s1, String s2);
}
