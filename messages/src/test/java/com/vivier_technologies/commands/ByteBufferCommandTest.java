package com.vivier_technologies.commands;

import com.vivier_technologies.utils.ByteBufferFactory;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ByteBufferCommandTest {

    @Test
    public void testSetAndReadDirect() {
        setAndCheck(ByteBufferFactory.nativeAllocateDirect(50));
    }

    @Test
    public void testSetAndReadNonDirect() {
        setAndCheck(ByteBufferFactory.nativeAllocate(50));
    }

    public void setAndCheck(ByteBuffer buffer) {
        ByteBufferCommand command = new ByteBufferCommand();
        assertNull(command.getData());
        buffer.putShort(CommandHeader.TYPE, (short)1);
        buffer.put(CommandHeader.SRC, "TESTTEST".getBytes(), 0, CommandHeader.SRC_LEN);

        byte[] body = "TESTTHISWORKS".getBytes();
        buffer.put(Command.CMD_BODY_START, body, 0, body.length);
        buffer.putInt(CommandHeader.CMD_SEQ, 1);
        buffer.putInt(CommandHeader.CMD_LEN, Command.CMD_BODY_START + body.length);

        command.setData(buffer);
        assertEquals(Command.CMD_BODY_START + body.length, command.getHeader().getLength());
        assertEquals(1, command.getHeader().getType());
        assertEquals(1, command.getHeader().getSequence());
        assertEquals("TESTTEST", new String(command.getHeader().getSource()));
        byte[] readBody = new byte[100];
        command.getData().get(Command.CMD_BODY_START, readBody, 0,
                command.getHeader().getLength() - Command.CMD_BODY_START);
        assertEquals(new String(body), new String(readBody, 0,
                command.getHeader().getLength() - Command.CMD_BODY_START));
    }

    @Test
    public void testHeaderSet() {
        ByteBuffer buffer = ByteBufferFactory.nativeAllocate(1024);
        ByteBufferCommand command = new ByteBufferCommand(buffer);
        byte[] body = "TESTTHISWORKS".getBytes();
        byte[] src = "TESTTEST".getBytes();
        command.getData().put(Command.CMD_BODY_START, body);
        command.getHeader().setHeader(Command.CMD_BODY_START + body.length, (short)1, src, 1);
        assertEquals(Command.CMD_BODY_START + body.length, command.getHeader().getLength());
        assertEquals(1, command.getHeader().getType());
        assertEquals(new String(src), new String(command.getHeader().getSource()));
        assertEquals(1, command.getHeader().getSequence());
    }

}