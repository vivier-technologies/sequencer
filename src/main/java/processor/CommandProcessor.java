package processor;

import java.nio.ByteBuffer;

public interface CommandProcessor {

    boolean process(ByteBuffer command);

    String getName();
}
