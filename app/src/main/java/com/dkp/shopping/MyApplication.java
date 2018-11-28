package com.dkp.shopping;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dkp.shopping.dao.DaoMaster;
import com.dkp.shopping.dao.DaoSession;

/**
 * Created by dkp on 2018/10/25.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initGreenDao();
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper;
        helper = new DaoMaster.DevOpenHelper(this, "shopping.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    private DaoSession daoSession;
    public DaoSession getDaoSession() {
        return daoSession;
    }

}
