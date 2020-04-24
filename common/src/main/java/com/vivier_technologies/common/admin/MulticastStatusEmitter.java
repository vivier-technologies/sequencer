package com.vivier_technologies.common.admin;

import com.vivier_technologies.utils.Logger;

import javax.inject.Inject;

public class MulticastStatusEmitter implements StatusEmitter {

    @Inject
    public MulticastStatusEmitter(Logger logger) {

    }

    @Override
    public void sendStatus(Status s) {

    }
}
