package com.vivier_technologies.common.admin;

import java.io.IOException;

public interface AdminReceiver {

    void open() throws IOException;

    void close();

    void setListener(AdminListener listener);
}
