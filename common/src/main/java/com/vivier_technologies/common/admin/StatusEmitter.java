package com.vivier_technologies.common.admin;

import java.io.IOException;

public interface StatusEmitter {

    void sendStatus(Status s);

    void open() throws IOException;

    void close();
}
