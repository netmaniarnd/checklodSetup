package com.netmania.checklod.domain.viewmodel.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.data.repository.dao.DeviceDao;
import com.netmania.checklod.data.repository.entity.DeviceEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceViewModel extends AndroidViewModel {

    private DeviceDao dao;
    private ExecutorService executorService;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        dao = BaseApplication.mDb.deviceDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void deleteAll(){
        executorService.execute(() -> dao.deleteAll());
    }

    public LiveData<List<DeviceEntity>> getAll(){
        return dao.getAll();
    }

    public void save(DeviceEntity entity){
        executorService.execute(() -> dao.insert(entity));
    }

}
