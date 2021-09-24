package com.netmania.checklod.domain.listener;

public interface LoaderListener {
    void onLoaded(long count);
    void onFailed(int status, int newStatus);
}
