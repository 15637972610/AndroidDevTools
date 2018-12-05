package com.dkp.shopping.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 崩溃日志手动捕捉工具类
 * Created by  on 2018/12/5.
 */

public class APPOnCrash implements CrashHandler.OnCrash {
    public static final String LOG_FILE_DIR = "rouchi/log/crash";
    Context context;
    public APPOnCrash(Context cont) {
        this.context = cont;
    }

    @Override
    public void onPreTerminate(Thread thread, Throwable ex) {
//        XLog.d(" Crashed : " + Log.getStackTraceString(ex));
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        Date date = new Date();

        String packageName = context.getPackageName();

        try {
            writer.println("Date:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
            writer.println("\n");

            writer.println("AppPkgName:" + packageName);
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
                writer.println("VersionCode:" + packageInfo.versionCode);
                writer.println("VersionName:" + packageInfo.versionName);
                writer.println("Debug:" + (0 != (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)));
            } catch (Exception e) {
                writer.println("VersionCode:-1");
                writer.println("VersionName:null");
                writer.println("Debug:Unkown");
            }

            writer.println("PName:" + CommonUtils.getProcessName(context));
            writer.println("\n");

            writer.println("----------------------------------------System Infomation-----------------------------------");

            // 打印当前系统信息
            writer.println("Product: " + android.os.Build.PRODUCT);
            writer.println("CPU_ABI: " + android.os.Build.CPU_ABI);
            writer.println("TAGS: " + android.os.Build.TAGS);
            writer.println("VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE);
            writer.println("MODEL: " + android.os.Build.MODEL);
            writer.println("SDK: " + android.os.Build.VERSION.SDK);
            writer.println("VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE);
            writer.println("DEVICE: " + android.os.Build.DEVICE);
            writer.println("DISPLAY: " + android.os.Build.DISPLAY);
            writer.println("BRAND: " + android.os.Build.BRAND);
            writer.println("BOARD: " + android.os.Build.BOARD);
            writer.println("FINGERPRINT: " + android.os.Build.FINGERPRINT);
            writer.println("ID: " + android.os.Build.ID);
            writer.println("MANUFACTURER: " + android.os.Build.MANUFACTURER);
            writer.println("USER: " + android.os.Build.USER);

            writer.println("\n\n\n----------------------------------Exception---------------------------------------\n\n");
            writer.println("------Exception message:" + ex.getLocalizedMessage() + "\n");
            writer.println("------Exception StackTrace:");
            ex.printStackTrace(writer);

            if (FileUtils.isSdcardReady()) {
                File file = new File(Environment.getExternalStoragePublicDirectory(LOG_FILE_DIR),
                        "crash-app-" + new SimpleDateFormat("yyyy-MM-dd").format(date) + ".log");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                if (file.exists()) {
                    file.delete();
                }
                FileUtils.writeStringToFileImpl(file, sw.toString(), false);
            }
        } catch (Throwable e) {
//            XLog.e(" uncaughtException : " + Log.getStackTraceString(e));
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e1) {
            }
            try {
                if (sw != null) {
                    sw.flush();
                    sw.close();
                }
            } catch (Exception e2) {
            }
        }

    }

    @Override
    public void onTerminate(Thread thread, Throwable ex) {
        // 关闭当前应用
//        finishAllActivity();
//        XLog.e(" kill current process : " + android.os.Process.myPid());
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
