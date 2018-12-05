package com.dkp.shopping;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dkp.shopping.dao.DaoMaster;
import com.dkp.shopping.dao.DaoSession;
import com.dkp.shopping.utils.APPOnCrash;
import com.dkp.shopping.utils.CrashHandler;

/**
 * Created by dkp on 2018/10/25.
 */

public class MyApplication extends Application {
    private static Context context;
    private static long SLEEP_TIME = 3000;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CrashHandler.getInstance().init(SLEEP_TIME, new APPOnCrash(context)); //崩溃日志手动捕捉
        initGreenDao();
    }

    public static Context getContext() {
        return context;
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
