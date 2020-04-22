package com.vivier_technologies.commands;

import java.nio.ByteBuffer;

public interface Command {

    int CMD_BODY_START = CommandHeader.CMD_HEADER_LEN;

    CommandHeader getHeader();

    //TODO consider whether bytebuffer is the right abstraction here
    ByteBuffer getData();

}
