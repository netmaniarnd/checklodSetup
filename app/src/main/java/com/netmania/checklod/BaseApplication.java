package com.netmania.checklod;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.netmania.checklod.data.repository.AppDataBase;

public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    private static BaseApplication mInstance;
    private static Context mAppContext;
    public static AppDataBase mDb;

    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate::");
        mInstance = this;
        mAppContext = getApplicationContext();
        mDb = AppDataBase.getDatabase(mAppContext);
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

}
