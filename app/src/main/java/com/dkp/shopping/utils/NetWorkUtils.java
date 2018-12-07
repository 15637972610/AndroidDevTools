package com.dkp.shopping.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 类的用途 联网判断
 * Created by Administrator on 2018/7/19.
 */

public class NetWorkUtils {
    private static final String TAG = "AndroidUtil";
    /**
     * 网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (networkInfo != null) {
            Log.i(TAG, "-------networkInfo.isConnected------" + networkInfo.isConnected() + "-----");
        } else {
            Log.i(TAG, "-------networkInfo------" + networkInfo + "-----");
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (networkInfo != null) {
            Log.i(TAG, "-------networkInfo.isConnected------" + networkInfo.isConnected() + "-----");
        } else {
            Log.i(TAG, "-------networkInfo------" + networkInfo + "-----");
        }

        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }
    /**
     * Wifi网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnectedByWifi(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取当前网络类型
     *
     * @param context 上下文
     * @return
     */
    public static String getNetWorkType(Context context) {
        String strNetworkType = "";
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                Log.e("getNetWorkType", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
                        // 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
                        // 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
                        // 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
                        // 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
                        // 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
                                || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                Log.e("getNetWorkType", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        Log.e("getNetWorkType", "Network Type : " + strNetworkType);

        return strNetworkType;
    }

    /**
     * 获取设备ID号(国际移动设备身份码，储存在移动设备中，全球唯一的一组号码)
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        return null;
//        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获取Sim卡的Imsi号(国际移动用户识别码，储存在SIM卡中，国际上为唯一识别一个移动用户所分配的号码)
     *
     * @return
     */
    public static String getImsi(Context context) {
        return null;
//        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    private static int parserXml(String xml) {
        int sumSim = 2;
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
        try {
            XmlPullParser parser = Xml.newPullParser();
            Log.e(TAG, " **********************");
            parser.setInput(is, "utf-8");
            Log.e(TAG, " **********xml************");
            int event = parser.getEventType();
            Log.e(TAG, "   event:" + event + "  xml:" + xml);
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("TTQValue".equals(parser.getName())) {
                            String value = parser.nextText();
                            sumSim = Integer.parseInt(value);
                            Log.e(TAG, " getSimInfo() sumSim*****sumSim******sumSim:" + sumSim);
                        }
                        break;
                }
                event = parser.next();
                Log.e(TAG, "   ************event=" + event);
            }
        } catch (Exception e) {
            Log.e(TAG, "   e:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sumSim;
    }

}
