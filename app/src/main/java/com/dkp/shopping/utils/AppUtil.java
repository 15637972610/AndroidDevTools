package com.dkp.shopping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppUtil {


    /**
     * 获取本应用的版本名称
     */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }


    /**
     * 获取指定包名应用的版本名称
     */
    public static String getVersionName(Context context, String packageName) {
        String versionName = "1.0.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            versionName = null;
        }
        return versionName;
    }

    /**
     * 获取客户端版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 安装APK
     */
    public static void installAPK(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider",
                    apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        } else {
            uri = Uri.fromFile(apkFile);
        }
        Log.i("-----", "installAPK: uri=" + uri);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);// 安装
    }

    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }


    /**
     * 获取字符串的MD5
     */
    public static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte aDigest : digest) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = aDigest & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }


    /**
     * 隐藏输入法（根据activity当前焦点所在控件的WindowToken）
     */
    public static void hideSoftInput(Activity activity, View editText) {
        View view;
        if (editText == null) {
            view = activity.getCurrentFocus();
        } else {
            view = editText;
        }
        if (view != null) {
            InputMethodManager inputMethod = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethod != null) {
                inputMethod.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
