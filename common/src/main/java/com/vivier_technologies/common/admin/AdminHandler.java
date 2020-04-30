package com.vivier_technologies.common.admin;

public interface AdminHandler {

    void onGoActive();

    void onGoPassive();

    void onShutdown();

    void onStatusRequest();
}
