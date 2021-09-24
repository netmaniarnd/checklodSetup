package com.netmania.checklod.domain.viewmodel.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.netmania.checklod.data.repository.dao.TripDao;
import com.netmania.checklod.data.repository.entity.TripEntity;
import com.netmania.checklod.presentation.BaseActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;

public class TripViewModel extends AndroidViewModel {

    private TripDao mDao;

    public TripViewModel(@NonNull Application application) {
        super(application);
        mDao = BaseActivity.mDb.tripDao();
    }

    public void insert(TripEntity entity) {
        new insertAsyncTask(mDao).execute(entity);
    }

    public LiveData<List<TripEntity>> getAllTrip() {
        return mDao.getAllTrip();
    }

    public LiveData<List<TripEntity>> getTempAll(int min, int max) {
        return mDao.getTempAll(min, max);
    }

    public List<TripEntity> getAll() throws ExecutionException, InterruptedException {
        return new getAllAsyncTask(mDao).execute().get();
    }

    public TripEntity getTripInfoByMac(String mac) throws ExecutionException, InterruptedException {
        return new getTripByMacAsyncTask(mDao).execute(mac).get();
    }

    public TripEntity getTripInfoByStickerNo(String stickerNo) throws ExecutionException, InterruptedException {
        return new getTripByStickerNoAsyncTask(mDao).execute(stickerNo).get();
    }

    @SneakyThrows
    public int getCount() {
        return getAll() == null ? 0 : getAll().size();
    }

    public void update(TripEntity entity) {
        new updateAsyncTask(mDao).execute(entity);
    }

    public void delete(String mac) {
        new deleteAsyncTask(mDao).execute(mac);
    }

    private static class insertAsyncTask extends AsyncTask<TripEntity, Void, Void> {
        private TripDao mDao;

        public insertAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(TripEntity... tripEntities) {
            mDao.insertTrip(tripEntities[0]);
            return null;
        }
    }

    private static class getAllAsyncTask extends AsyncTask<Void, Void, List<TripEntity>> {
        private TripDao mDao;

        getAllAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected List<TripEntity> doInBackground(Void... voids) {
            return mDao.getAll();
        }
    }

    private static class getTripByMacAsyncTask extends AsyncTask<String, Void, TripEntity> {
        private TripDao mDao;

        getTripByMacAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected TripEntity doInBackground(String... strings) {
            return mDao.getTripInfoByMac(strings[0]);
        }
    }

    private static class getTripByStickerNoAsyncTask extends AsyncTask<String, Void, TripEntity> {
        private TripDao mDao;

        getTripByStickerNoAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected TripEntity doInBackground(String... strings) {
            return mDao.getTripInfoByStickerNo(strings[0]);
        }
    }

    private static class updateAsyncTask extends AsyncTask<TripEntity, Void, Void> {
        private TripDao mDao;

        public updateAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(TripEntity... tripEntities) {
            mDao.updateTrip(tripEntities[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
        private TripDao mDao;

        public deleteAsyncTask(TripDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.deleteTrip(strings[0]);
            return null;
        }
    }

}
