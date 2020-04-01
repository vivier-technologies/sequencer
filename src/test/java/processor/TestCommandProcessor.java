package processor;

import java.nio.ByteBuffer;

public class TestCommandProcessor implements CommandProcessor {

    @Override
    public boolean process(ByteBuffer command) {
        return false;
    }

    @Override
    public String getName() {
        return "Test";
    }
}
