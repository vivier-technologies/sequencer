package com.vivier_technologies.admin;

import java.nio.ByteBuffer;

public interface Status {

    //TODO consider whether bytebuffer is the right abstraction here
    ByteBuffer getData();
}
