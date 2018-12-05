package com.dkp.shopping.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author dkp on 2017/5/25 10:30.
 */

public class CommonUtils {
    public static int getScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        if (null == context) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if(appProcesses != null){
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

    //判断文本控件文字宽度是否超过屏幕
    public static boolean isCover(Context context, TextView textView){
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float scale = context.getResources().getDisplayMetrics().density;
        int margn =  (int) (20 * scale + 0.5f); //margn left 20 right 20
        float textWidth = textView.getPaint().measureText(textView.getText().toString());
        if((textWidth + margn*2 )>screenWidth){
            return  true;
        }
        return  false;

    }
}
