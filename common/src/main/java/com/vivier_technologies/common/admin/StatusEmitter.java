package com.vivier_technologies.common.admin;

import com.vivier_technologies.admin.Status;

import java.io.IOException;

public interface StatusEmitter {

    void sendStatus(Status s) throws IOException;

    void open() throws IOException;

    void close();
}
