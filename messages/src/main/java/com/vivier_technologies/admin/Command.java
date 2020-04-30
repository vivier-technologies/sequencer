package com.vivier_technologies.admin;

public interface Command {

    int INSTANCE_NAME_LEN = 16;

    interface Type {
        short STATUS = 0;
        short GO_ACTIVE = 1;
        short GO_PASSIVE = 2;
        short SHUTDOWN = 3;
    }

    int TYPE = 0;
    int INSTANCE_NAME = 2;

    byte[] getInstance();

    short getType();
}
