package com.vivier_technologies.common.admin;

import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;

public class MulticastStatusEmitter implements StatusEmitter {

    @Inject
    public MulticastStatusEmitter(Logger logger, Configuration configuration) {

    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

    }

    @Override
    public void sendStatus(Status s) {

    }
}
