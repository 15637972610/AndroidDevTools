package com.dkp.shopping;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.dkp.shopping.dao.DaoMaster;
import com.dkp.shopping.dao.DaoSession;
import com.dkp.shopping.service.UploadLogService;
import com.dkp.shopping.utils.APPOnCrash;
import com.dkp.shopping.utils.CrashHandler;
import com.dkp.shopping.utils.FileUtil;
import com.dkp.shopping.utils.MyBackupStrategy;
import com.dkp.shopping.utils.NetWorkUtils;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;
import com.elvishew.xlog.printer.flattener.LogFlattener;
import com.lzy.okgo.OkGo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by dkp on 2018/10/25.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context mContext;
    private static long SLEEP_TIME = 3000;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        CrashHandler.getInstance().init(SLEEP_TIME, new APPOnCrash(mContext)); //崩溃日志手动捕捉
        initGreenDao();
        initLog();
    }

    public static Context getContext() {
        return mContext;
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

    /**
     * OkGo初始化
     */
    private void initOkHttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        //必须调用初始化
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build());
    }

    /**
     * 方法名：initLog()
     * 功  能：初始化log
     */
    private void initLog() {
        LogConfiguration configuration = new LogConfiguration.Builder().logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.ALL).tag("DKP_TAG").build();
        AndroidPrinter androidPrinter = new AndroidPrinter();
        /**
         * 初始化的时候设置filePrinter为全局配置
         * 当局部需要把日志保存在不同的文件时可以通过 XLog.printers(filePrinter).tag(TAG).build()
         * 的方式实现，
         * 二者同时使用时互不影响
         */
        FilePrinter filePrinter = new FilePrinter.Builder(
                new File(Environment.getExternalStorageDirectory(),
                        "dkpdemo/xlog").getPath()).fileNameGenerator(
                new DateFileNameGenerator()).logFlattener(new ClassicFlattener()).backupStrategy(new MyBackupStrategy(1024*1024*10)).build();
        XLog.init( configuration, androidPrinter,filePrinter);
        XLog.d(TAG,"application initLog");
        // 注册网络变化监听，用于上传崩溃日志
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkStatusReceiver, filter);
//        // 开启子线程清除旧日志
//        new RxAsyncHelper("").runInThread(new Func1() {
//            @Override
//            public Object call(Object o) {
//                try {
//                    Thread.sleep(5000);
//                    UploadLogService.deleteOldLog(mContext);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }).subscribe();

    }

    /**
     * 网络变化监听，用于上传崩溃日志
     */
    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetWorkUtils.isNetworkConnectedByWifi(context)) {
                XLog.d(TAG, "网络改变，当前类型为wifi，上传崩溃日志");
                String number = null;
                try {
                    number = "18301186819";
                } catch (Exception e) {
                }
                if (!TextUtils.isEmpty(number)) {
                    String uploadFileName = "." + number + "." + new SimpleDateFormat("yyyy.MM.dd").format(new Date())+".log";
                    startService(UploadLogService.createUploadExceptionIntent(context, uploadFileName,"18301186819"/*LoginUtils.getInstance().getLoginUserName()*/));
                }

            }
        }
    };
    private DaoSession daoSession;
    public DaoSession getDaoSession() {
        return daoSession;
    }

}
