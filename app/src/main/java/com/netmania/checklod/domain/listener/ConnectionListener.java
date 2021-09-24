package com.netmania.checklod.domain.listener;

public interface ConnectionListener {
    void onConnect();
    void onLoaded(int type, byte[] bytes, String mac);
    void onFailed();
}
