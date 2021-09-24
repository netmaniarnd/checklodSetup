package com.netmania.checklod.domain.viewmodel.usecase;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.gun0912.tedpermission.TedPermission;
import com.netmania.checklod.R;
import com.netmania.checklod.data.constant.Permissions;
import com.netmania.checklod.domain.listener.PermissionListener;

import java.util.List;

public class PermissionUseCase {

    public static String TAG = PermissionUseCase.class.getSimpleName();

    /**
     * Permission 확인
     */
    public static void setUserPermission(Context context, PermissionListener listener) {
        Log.e(TAG, "VERSION : " + Build.VERSION.SDK_INT);
        String msg = context.getResources().getString(R.string.permission_denied_msg);
        com.gun0912.tedpermission.PermissionListener permissionListener = new com.gun0912.tedpermission.PermissionListener() {
            @Override
            public void onPermissionGranted() {
                listener.onPermissionGranted();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                listener.onPermissionDenied();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            TedPermission.with(context)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage(msg)
                    .setPermissions(
                            Permissions.Q_PERMISSIONS
                    )
                    .check();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TedPermission.with(context)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage(msg)
                    .setPermissions(
                            Permissions.M_PERMISSIONS
                    )
                    .check();
        }
    }
}
