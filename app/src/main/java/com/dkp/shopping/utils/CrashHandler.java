package com.dkp.shopping.utils;

import android.os.Looper;
import android.os.SystemClock;

/**
 * 文件名：CrashHandler
 * 描    述：以进程为单位，处理未捕捉的异常  崩溃日志手动捕捉工具类
 * 时    间：2018/8/29.
 */

public class CrashHandler {

    private long mPreTerminateMillis;
    private OnCrash mOnCrash;

    private CrashHandler() {

    }

    // 获取CrashHandler实例 ,单例模式
    public static CrashHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //静态内部类,只有在装载该内部类时才会去创建单例对象
    private static class SingletonHolder {
        private static final CrashHandler INSTANCE = new CrashHandler();
    }

    /**
     * 方法名：init(long preTerminateMillis, OnCrash onCrash)
     * 功   能：初始化
     * 参   数：long preTerminateMillis 休眠事件, OnCrash onCrash 终止
     * 返回值：无
     */
    public void init(long preTerminateMillis, OnCrash onCrash) {
        mPreTerminateMillis = preTerminateMillis;
        mOnCrash = onCrash;
        // 为所在进程内所有Thread设置一个默认的UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable ex) {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (mOnCrash != null) {
                            mOnCrash.onPreTerminate(thread, ex);
                        }
                        Looper.loop();
                    }
                }.start();
                SystemClock.sleep(mPreTerminateMillis);
                if (mOnCrash != null) {
                    mOnCrash.onTerminate(thread, ex);
                }
            }
        });
    }

    public interface OnCrash {
        /**
         * 准备终止操作
         *
         * @param thread
         * @param ex
         */
        void onPreTerminate(Thread thread, Throwable ex);

        /**
         * 终止操作
         *
         * @param thread
         * @param ex
         */
        void onTerminate(Thread thread, Throwable ex);
    }
}
