package com.netmania.checklod.domain.listener;

public interface PermissionListener {
    void onPermissionGranted();
    void onPermissionDenied();
}
