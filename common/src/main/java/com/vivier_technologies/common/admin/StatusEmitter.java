package com.vivier_technologies.common.admin;

import java.io.IOException;

public interface StatusEmitter {

    void sendStatus(boolean isActive);

    void open() throws IOException;

    void close();
}
