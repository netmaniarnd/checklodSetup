package com.netmania.checklod.domain.viewmodel.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.data.repository.dao.LogDao;
import com.netmania.checklod.data.repository.dao.LogDao;
import com.netmania.checklod.data.repository.entity.LogEntity;
import com.netmania.checklod.data.repository.entity.TripEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;

public class LogViewModel extends AndroidViewModel {

    private LogDao mDao;

    public LogViewModel(@NonNull Application application) {
        super(application);
        mDao = BaseApplication.mDb.logDao();
    }

    public void insert(LogEntity entity) {
        new insertAsyncTask(mDao).execute(entity);
    }

    public LiveData<List<LogEntity>> getAllLog(String mac) {
        return mDao.getAllLog(mac);
    }

    public List<LogEntity> getAll(String mac) throws ExecutionException, InterruptedException {
        return new getAllAsyncTask(mDao).execute(mac).get();
    }

    @SneakyThrows
    public int getCount(String mac) {
        return getAll(mac) == null ? 0 : getAll(mac).size();
    }

    public LogEntity getDeviceLog(String mac, int sequence) throws ExecutionException, InterruptedException {
        return new getDeviceLogAsyncTask(mDao).execute(mac, String.valueOf(sequence)).get();
    }

    public LogEntity getFirstLog(String mac) throws ExecutionException, InterruptedException {
        return new getFirstLogAsyncTask(mDao).execute(mac).get();
    }

    public LogEntity getLastLog(String mac) throws ExecutionException, InterruptedException {
        return new getLastLogAsyncTask(mDao).execute(mac).get();
    }

    public LogEntity getMaxTempLog(String mac) throws ExecutionException, InterruptedException {
        return new getTempMaxLogAsyncTask(mDao).execute(mac).get();
    }

    public LogEntity getMinTempLog(String mac) throws ExecutionException, InterruptedException {
        return new getTempMinLogAsyncTask(mDao).execute(mac).get();
    }

    public List<LogEntity> getNotSent(String mac) throws ExecutionException, InterruptedException {
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

    private static class insertAsyncTask extends AsyncTask<LogEntity, Void, Void> {
        private LogDao mDao;

        public insertAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(LogEntity... logEntities) {
            mDao.insertDeviceLog(logEntities[0]);
            return null;
        }
    }

    private static class getAllAsyncTask extends AsyncTask<String, Void, List<LogEntity>> {
        private LogDao mDao;

        getAllAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<LogEntity> doInBackground(String... strings) {
            return mDao.getAll(strings[0]);
        }
    }

    private static class getDeviceLogAsyncTask extends AsyncTask<String, Void, LogEntity> {
        private LogDao mDao;

        getDeviceLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected LogEntity doInBackground(String... strings) {
            return mDao.getDeviceLog(strings[0], Integer.parseInt(strings[1]));
        }
    }

    private static class getFirstLogAsyncTask extends AsyncTask<String, Void, LogEntity> {
        private LogDao mDao;

        getFirstLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected LogEntity doInBackground(String... strings) {
            return mDao.getFirstLog(strings[0]);
        }
    }

    private static class getLastLogAsyncTask extends AsyncTask<String, Void, LogEntity> {
        private LogDao mDao;

        getLastLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected LogEntity doInBackground(String... strings) {
            return mDao.getLastLog(strings[0]);
        }
    }

    private static class getTempMaxLogAsyncTask extends AsyncTask<String, Void, LogEntity> {
        private LogDao mDao;

        getTempMaxLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected LogEntity doInBackground(String... strings) {
            return mDao.getMaxTempLog(strings[0]);
        }
    }

    private static class getTempMinLogAsyncTask extends AsyncTask<String, Void, LogEntity> {
        private LogDao mDao;

        getTempMinLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected LogEntity doInBackground(String... strings) {
            return mDao.getMinTempLog(strings[0]);
        }
    }

    private static class getNotSentLogAsyncTask extends AsyncTask<String, Void, List<LogEntity>> {
        private LogDao mDao;

        getNotSentLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<LogEntity> doInBackground(String... strings) {
            return mDao.getNotSent(strings[0]);
        }
    }

    private static class getLostDataLogAsyncTask extends AsyncTask<String, Void, List<Integer>> {
        private LogDao mDao;

        getLostDataLogAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected List<Integer> doInBackground(String... strings) {
            return mDao.getLostData(strings[0]);
        }
    }

    private static class updateAsyncTask extends AsyncTask<String, Void, Void> {
        private LogDao mDao;

        public updateAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.updateDeviceSent(strings[0], Integer.parseInt(strings[1]));
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
        private LogDao mDao;

        public deleteAsyncTask(LogDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.deleteLog(strings[0]);
            return null;
        }
    }

}