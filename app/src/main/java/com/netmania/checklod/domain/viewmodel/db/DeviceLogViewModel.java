package com.netmania.checklod.domain.viewmodel.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.data.repository.dao.DeviceLogDao;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;

public class DeviceLogViewModel extends AndroidViewModel {

    private DeviceLogDao mDao;

    public DeviceLogViewModel(@NonNull Application application) {
        super(application);
        mDao = BaseApplication.mDb.deviceLogDao();
    }

    public void insert(DeviceLogEntity entity) {
        new insertAsyncTask(mDao).execute(entity);
    }

    public List<DeviceLogEntity> getAll(String mac) throws ExecutionException, InterruptedException {
        return new getAllAsyncTask(mDao).execute(mac).get();
    }

    @SneakyThrows
    public int getCount(String mac) {
        return getAll(mac) == null ? 0 : getAll(mac).size();
    }

    public DeviceLogEntity getDeviceLog(String mac, int sequence) throws ExecutionException, InterruptedException {
        return new getDeviceLogAsyncTask(mDao).execute(mac, String.valueOf(sequence)).get();
    }

    public DeviceLogEntity getFirstLog(String mac) throws ExecutionException, InterruptedException {
        return new getFirstLogAsyncTask(mDao).execute(mac).get();
    }

    public DeviceLogEntity getLastLog(String mac) throws ExecutionException, InterruptedException {
        return new getLastLogAsyncTask(mDao).execute(mac).get();
    }

    public DeviceLogEntity getMaxTempLog(String mac) throws ExecutionException, InterruptedException {
        return new getTempMaxLogAsyncTask(mDao).execute(mac).get();
    }

    public DeviceLogEntity getMinTempLog(String mac) throws ExecutionException, InterruptedException {
        return new getTempMinLogAsyncTask(mDao).execute(mac).get();
    }

    public List<DeviceLogEntity> getNotSent(String mac) throws ExecutionException, InterruptedException {
        return new getNotSentLogAsyncTask(mDao).execute(mac).get();
    }

    public List<Integer> getLostData(String mac) throws ExecutionException, InterruptedException {
        return new getLostDataLogAsyncTask(mDao).execute(mac).get();
    }

    public void update(String mac, int sequence) {
        new updateAsyncTask(mDao).execute(mac, String.valueOf(sequence));
    }

    public void delete(String mac) {
        new deleteAsyncTask(mDao).execute(mac);
    }

    private static class insertAsyncTask extends AsyncTask<DeviceLogEntity, Void, Void> {
        private DeviceLogDao mDao;

        public insertAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(DeviceLogEntity... deviceLogEntities) {
            mDao.insertDeviceLog(deviceLogEntities[0]);
            return null;
        }
    }

    private static class getAllAsyncTask extends AsyncTask<String, Void, List<DeviceLogEntity>> {
        private DeviceLogDao mDao;

        getAllAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<DeviceLogEntity> doInBackground(String... strings) {
            return mDao.getAll(strings[0]);
        }
    }

    private static class getDeviceLogAsyncTask extends AsyncTask<String, Void, DeviceLogEntity> {
        private DeviceLogDao mDao;

        getDeviceLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected DeviceLogEntity doInBackground(String... strings) {
            return mDao.getDeviceLog(strings[0], Integer.parseInt(strings[1]));
        }
    }

    private static class getFirstLogAsyncTask extends AsyncTask<String, Void, DeviceLogEntity> {
        private DeviceLogDao mDao;

        getFirstLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected DeviceLogEntity doInBackground(String... strings) {
            return mDao.getFirstLog(strings[0]);
        }
    }

    private static class getLastLogAsyncTask extends AsyncTask<String, Void, DeviceLogEntity> {
        private DeviceLogDao mDao;

        getLastLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected DeviceLogEntity doInBackground(String... strings) {
            return mDao.getLastLog(strings[0]);
        }
    }

    private static class getTempMaxLogAsyncTask extends AsyncTask<String, Void, DeviceLogEntity> {
        private DeviceLogDao mDao;

        getTempMaxLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected DeviceLogEntity doInBackground(String... strings) {
            return mDao.getMaxTempLog(strings[0]);
        }
    }

    private static class getTempMinLogAsyncTask extends AsyncTask<String, Void, DeviceLogEntity> {
        private DeviceLogDao mDao;

        getTempMinLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected DeviceLogEntity doInBackground(String... strings) {
            return mDao.getMinTempLog(strings[0]);
        }
    }

    private static class getNotSentLogAsyncTask extends AsyncTask<String, Void, List<DeviceLogEntity>> {
        private DeviceLogDao mDao;

        getNotSentLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<DeviceLogEntity> doInBackground(String... strings) {
            return mDao.getNotSent(strings[0]);
        }
    }

    private static class getLostDataLogAsyncTask extends AsyncTask<String, Void, List<Integer>> {
        private DeviceLogDao mDao;

        getLostDataLogAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<Integer> doInBackground(String... strings) {
            return mDao.getLostData(strings[0]);
        }
    }

    private static class updateAsyncTask extends AsyncTask<String, Void, Void> {
        private DeviceLogDao mDao;

        public updateAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.updateDeviceSent(strings[0], Integer.parseInt(strings[1]));
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
        private DeviceLogDao mDao;

        public deleteAsyncTask(DeviceLogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.deleteLog(strings[0]);
            return null;
        }
    }

}