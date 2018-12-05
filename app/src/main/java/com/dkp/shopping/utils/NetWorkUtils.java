package com.dkp.shopping.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 类的用途 联网判断
 * Created by Administrator on 2018/7/19.
 */

public class NetWorkUtils {
    //判断网络是否连接
    public static boolean isNetWorkAvailable(Context context) {
        //网络连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //网络信息
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return true;
        }

        return false;
    }

}
