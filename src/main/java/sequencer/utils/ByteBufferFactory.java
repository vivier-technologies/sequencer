package sequencer.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Factory to ensure native byte order used when creating buffers
 * assumes of course that all machines running this are same architecture
 *
 */
public class ByteBufferFactory {

    public static ByteBuffer nativeAllocate(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
    }

    public static ByteBuffer nativeAllocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }
}
