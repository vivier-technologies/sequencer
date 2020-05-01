package com.vivier_technologies.admin;

import com.vivier_technologies.utils.ByteArrayUtils;

public interface Command {

    /**
     * Make sure instance name fits within the size allotted - shouldn't be called on the critical path
     * of any sensitive code
     *
     * @param s instance name
     * @return validated instance name as a byte array
     */
    static byte[] validateInstanceName(String s) {
        if(s.length() > INSTANCE_NAME_LEN)
            throw new IllegalArgumentException("Instance name is limited to 16 bytes");
        byte[] instanceName = new byte[INSTANCE_NAME_LEN];
        ByteArrayUtils.copyAndPadRightWithSpaces(s.getBytes(), instanceName, 0, INSTANCE_NAME_LEN);
        return instanceName;
    }

    interface Type {
        short STATUS = 0;
        short GO_ACTIVE = 1;
        short GO_PASSIVE = 2;
        short SHUTDOWN = 3;
    }

    int TYPE = 0;
    int INSTANCE_NAME = 2;
    int INSTANCE_NAME_LEN = 16;
    int ADMIN_CMD_LEN = INSTANCE_NAME + INSTANCE_NAME_LEN;

    byte[] ALL_INSTANCES = validateInstanceName("*");

    byte[] getInstance();

    short getType();
}
