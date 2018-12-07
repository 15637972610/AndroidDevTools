package com.dkp.shopping.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.dkp.shopping.utils.CommonUtils;
import com.dkp.shopping.utils.FileUtil;
import com.dkp.shopping.utils.NetWorkUtils;
import com.dkp.shopping.utils.NumberUtils;
import com.dkp.shopping.utils.SHA;
import com.dkp.shopping.utils.SharePreferenceUtils;
import com.dkp.shopping.utils.StringConstant;
import com.dkp.shopping.utils.ZipFileControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 上传日志
 */
public class UploadLogService extends Service {

    private static final String TAG = "UploadLogService";
    private String commentString = "Android Java Zip 测试.";// 压缩包注释
    private ZipFileControl mZipControl;

    //上传所有日志
    public static final String UPLOAD_ALL_LOG_ACTION = "com.dkp.shopping.action.upload.all.log";

    public static String LOG_DIR_GENERAL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/xlog";
    public static String LOG_DIR_LOGIN = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/login_log";
    public static String LOG_DIR_XLOG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/log/xlog";
    public static String LOG_DIR_CRASH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/crash";

    /**
     * 上传中状态判断
     */
    private boolean mIsUploading = false;
    /**
     * 非wifi下是否上传log
     */
    private boolean mMobileNetworkUploadEnable = false;
    /**
     * 是否上传普通log
     */
    private boolean mUploadGeneralLog = false;
    /**
     * 是否上传登录相差的log
     */
    private boolean mUploadLoginLog = false;
    /**
     * 上传异常的log
     */
    private boolean mUploadExceptionLog = false;

    /**
     * 指定的待上传文件
     */
    private String mUploadFileName = null;

    private static final String UPLOAD_USER_MOBILE = "upload_user_mobile";

    private static final String UPLOAD_GENERAL_LOG = "upload_general_log";
    private static final String UPLOAD_EXCEPTION_LOG = "upload_exception_log";
    private static final String UPLOAD_LOGIN_LOG = "upload_login_log";
    private static final String UPLOAD_LOG_BY_MOBILE_NETWORK = "upload_log_by_mobile_network";
    private static final String UPLOAD_EXCEPTION_LOG_FILE_NAME = "upload_exception_log_file_name";
    // TODO: 2017/3/29 for test
    // public static final String LOG_UPLOAD_SERVER_URL = "http://221.176.29.154/services/ngcc/datareporting";
    public static final String LOG_UPLOAD_SERVER_URL = "http://220.231.2.33:8765/services/ngcc/datareporting";

    private static final long TWO_DATE_TIME_MILLIS = 1000 * 60 * 60 * 24 * 2; //2天的毫秒数

    private static final String UPLOAD_APP_KEY = "a63425e0597b11e88b1b28924a34aba8";
    private static final String UPLOAD_APP_SECRET = "a634406b597b11e88b1b28924a34aba8";
    private static final String URL = "http://221.176.34.113:8180/app_log/log/logupload/pushLogs";
    private static final String UPLOAD_SDK_FROM = "android";
    private static final String TYPE_OTHER = "1";
    private static final String TYPE_CRASH = "2";

    String mobile ="";

    Handler mHandlre = new Handler();

    public static final String KEY_FOR_UPLOAD_STATE_CODE = "key_for_upload_state_code";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!NetWorkUtils.isNetworkConnected(this)) {
            Log.d(TAG, "Don't Upload Log because network is off");
            return super.onStartCommand(intent, flags, startId);
        }
        mobile = "";
        if (intent != null) {
            mMobileNetworkUploadEnable = intent.getBooleanExtra(UPLOAD_LOG_BY_MOBILE_NETWORK, false);
            mUploadGeneralLog = intent.getBooleanExtra(UPLOAD_GENERAL_LOG, false);
            mUploadExceptionLog = intent.getBooleanExtra(UPLOAD_EXCEPTION_LOG, false);
            mUploadLoginLog = intent.getBooleanExtra(UPLOAD_LOGIN_LOG, false);
            mUploadFileName = intent.getStringExtra(UPLOAD_EXCEPTION_LOG_FILE_NAME);
            mobile = intent.getStringExtra(UPLOAD_USER_MOBILE);
        }
        Log.d(TAG, "intent="+intent);

        //添加网络判断，非WIFI网络时不发送日志
        boolean isWifi = NetWorkUtils.isNetworkConnectedByWifi(this);

        // 登录相关log上传
        if (mUploadLoginLog) {
            // TODO: 2017/3/28  MODE_MULTI_PROCESS does not work reliably， maybe use ContentProvider.
//            SharedPreferences sharedPreferences = this.getSharedPreferences("rcs_sp", Context.MODE_MULTI_PROCESS);
//            if (sharedPreferences.getBoolean(IS_NEED_UPLOADLOGIN, false)) {
                new LoginLogUploadTask(mobile).execute("");
                return super.onStartCommand(intent, flags, startId);
//            }
        }

        // 崩溃相关log上传
        if (mUploadExceptionLog && (mMobileNetworkUploadEnable || isWifi)) {
            new ExceptionLogUploadTask(mobile).execute("");
            return super.onStartCommand(intent, flags, startId);
        }

        // 普通log上传(--非上传登录或崩溃log时--)
        if (mUploadGeneralLog && (mMobileNetworkUploadEnable || isWifi)) {
            if (!mIsUploading) {
                new GeneralLogUploadTask().execute("");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 上传普通log
     */
    public class GeneralLogUploadTask extends AsyncTask<String, Void, UploadResultModel> {
        @Override
        protected void onPreExecute() {
            mIsUploading = true;
            super.onPreExecute();
        }

        @Override
        protected UploadResultModel doInBackground(String... params) {
            // 删除旧文件
            deleteOldLog(getApplication());

            UploadResultModel model = initLogUpload(LOG_DIR_GENERAL);
            if (!model.logBackUp()) {
                Log.d(TAG, "logBackUp FAIL general");
                return model;
            }
            int send = -1;
            try {
                String updateLogUrl = getUploadLogUrl2(getApplication());
                send = sendLogFile(updateLogUrl,model.archiveString + "/" + model.mZipName + ".zip",TYPE_OTHER,mobile);
//                send = send(LOG_UPLOAD_SERVER_URL, params[0], archiveString + "/" + mZipName + ".zip");
                Log.d(TAG, "send: " + send);
            } catch (Exception e) {
                e.getMessage();
                return model;
            }
            model.result = send == -1 ? false : true;
            return model;
        }

        @Override
        protected void onPostExecute(UploadResultModel model) {
            if (model.result) {
                File mfile = new File(model.archiveString);
                File[] fl = mfile.listFiles();
                for (int i = 0; i < fl.length; i++) {
                    if (fl[i].toString().endsWith(".zip")) {
                        fl[i].delete();
                        Log.e(TAG, "delete success " + fl[i]);
                    }
                }
            }
            super.onPostExecute(model);
            UploadLogService.this.stopSelf();

            mIsUploading = false;
        }
    }

    /**
     * 登录log上传
     */
    public class LoginLogUploadTask extends AsyncTask<String, Void, List<UploadResultModel>> {
        private String mobile = "";

        public LoginLogUploadTask(String phone) {
            if (!TextUtils.isEmpty(phone)) {
                mobile = phone;
            }
        }

        @Override
        protected void onPreExecute() {
            mIsUploading = true;
            super.onPreExecute();
        }

        @Override
        protected List<UploadResultModel> doInBackground(String... params) {
            deleteOldLog(getApplication());
            List<UploadResultModel> list = new ArrayList<>();
            String updateLogUrl = getUploadLogUrl2(getApplication());
            UploadResultModel model = initLogUpload(LOG_DIR_LOGIN);
            if (!model.logBackUp()) {
                Log.d(TAG, "logBackUp FAIL ,login");
                list.add(model);
            }else{
                int send = -1;
                try {
                    send = sendLogFile(updateLogUrl,model.archiveString + "/" + model.mZipName + ".zip",TYPE_OTHER,mobile);//发送 mtc log
//                send = send(LOG_UPLOAD_SERVER_URL, params[0], archiveString + "/" + mZipName + ".zip");
                    Log.d(TAG, "send: " + send);
                } catch (Exception e) {
                    e.getMessage();
                }
                model.result = send == -1 ? false : true;
                list.add(model);
            }

            //Xlog 上传
            UploadResultModel modelXlog = initLogUpload(LOG_DIR_XLOG);
            if (!modelXlog.logBackUp()) {
                Log.d(TAG, "logBackUp FAIL ,xlog");
                list.add(modelXlog);
            }else{
                int send = -1;
                try {
                    send = sendLogFile(updateLogUrl,modelXlog.archiveString + "/" + modelXlog.mZipName + ".zip",TYPE_OTHER,mobile);//发送 mtc log
//                send = send(LOG_UPLOAD_SERVER_URL, params[0], archiveString + "/" + mZipName + ".zip");
                    Log.d(TAG, "send: " + send);
                } catch (Exception e) {
                    e.getMessage();
                }
                modelXlog.result = send == -1 ? false : true;
                list.add(modelXlog);
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<UploadResultModel> list) {
            boolean isUploadSuccess = true;
            for(UploadResultModel model :list){
                if (model.result) {
                    File mfile = new File(model.archiveString);
                    File[] fl = mfile.listFiles();
                    for (int i = 0; i < fl.length; i++) {
                        if (fl[i].toString().endsWith(".zip")) {
                            fl[i].delete();
                            Log.e(TAG, "delete success " + fl[i]);
                        }
                    }
                    if(model.type == UploadType.LOGIN){
                        // 删除已上传完成的日志
                        FileUtil.deletE(new File(LOG_DIR_LOGIN));
                    }else if(model.type == UploadType.XLOG){
                        FileUtil.deletE(new File(LOG_DIR_XLOG));
                    }

                }else{
                    isUploadSuccess = false;
                }
            }
            Intent intent = new Intent(UPLOAD_ALL_LOG_ACTION);
            if(isUploadSuccess){
                intent.putExtra(KEY_FOR_UPLOAD_STATE_CODE , 1);

            }else{
                intent.putExtra(KEY_FOR_UPLOAD_STATE_CODE , 2);
            }
            UploadLogService.this.sendBroadcast(intent);

            super.onPostExecute(list);
            UploadLogService.this.stopSelf();

            mIsUploading = false;
        }
    }



    /**
     * 异常log上传
     */
    public class ExceptionLogUploadTask extends AsyncTask<String, Void, Boolean> {
        private String mobile = "";

        public ExceptionLogUploadTask(String phone) {
            if (!TextUtils.isEmpty(phone)) {
                mobile = phone;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            deleteOldLog(getApplication());

            int send = -1;
            try {
                File logfileDir = new File(LOG_DIR_CRASH);
                String fileName = null;
                if (!TextUtils.isEmpty(mUploadFileName)) {
                    fileName = mUploadFileName;
                } else {
                    fileName = "crash-app-" + new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()) + ".log";
                }
                File logFile = new File(logfileDir, fileName);
                String updateLogUrl = getUploadLogUrl2(getApplication());
//                if(!updateLogUrl.equals("")) {
                    send = sendLogFile(updateLogUrl, logFile.getAbsolutePath(), TYPE_CRASH, mobile);
                Log.d(TAG, "send: " + send);
//                }
            } catch (Exception e) {
                e.getMessage();
                return false;
            }
            return send == -1 ? false : true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                File logfileDir = new File(LOG_DIR_CRASH);
                String fileName = null;
                if (!TextUtils.isEmpty(mUploadFileName)) {
                    fileName = mUploadFileName;
                } else {
                    // fileName = "crash-app-" + new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()) + ".log";
                    return;
                }
                File logFile = new File(logfileDir, fileName);
                logFile.delete();
                mUploadFileName = null;
            }
            UploadLogService.this.stopSelf();
        }
    }

    private class UploadResultModel {
        UploadType type;
        String archiveString;
        String mZipName;
        String[] fileSrcStrings;// 指定压缩源，可以是目录或文件的数组
        boolean result = false;

        public UploadResultModel() {
        }

        /**
         * @return 备份log
         */
        protected boolean logBackUp() {
            Log.e(TAG, "start backup");
            try {
                mZipControl.writeByApacheZipOutputStream(fileSrcStrings, archiveString + "/" + mZipName + ".zip", commentString, mZipName + ".zip");
                Log.e(TAG, "mike zip success");
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public enum UploadType {
        LOGIN ,XLOG ,GENERAL
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mZipControl = null;
        mIsUploading = false;
        super.onDestroy();
    }


    /**
     * 发送请求
     *
     * @param url      请求地址
     * @param token    token
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public int send(String url, String token, String filePath) throws IOException {

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }

        java.net.URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setChunkedStreamingMode(1024 * 1024);

        Log.d(TAG, file + " send " + urlObj);
        // 设置请求头信息
        // 获取电话号码
        SharedPreferences sharedPreferences = this.getSharedPreferences("rcs_sp", Context.MODE_MULTI_PROCESS);
        String mobileNo = sharedPreferences.getString("auto_login", "");
        if (TextUtils.isEmpty(mobileNo)) {
            mobileNo = "13900000000";

        } else if (mobileNo.contains("+86")) {
            mobileNo = mobileNo.substring(3);
        }

        if (TextUtils.isEmpty(token)) {
            token = "XXXXXXXXXX";
        }
        Log.e(TAG, "mobileNo=" + mobileNo + "   , token =" + token);
        String publicUserID = String.format("tel:+86%s", mobileNo);
        con.setRequestProperty("X-3GPP-Intended-Identity", "\"" + publicUserID + "\"");
        con.setRequestProperty("Authorization", "UA token=\"" + token + "\"");

        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        // 请求正文信息
        StringBuilder sb = new StringBuilder();
        sb.append("--"); //
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        out.write(head);

        // 文件正文部分
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//
        out.write(foot);
        out.flush();
        out.close();

        // 读取服务器响应，必须读取,否则提交不成功
        Log.e(TAG, "con.getResponseCode()=" + con.getResponseCode());//返回200上传成功
        return con.getResponseCode();
    }


    /**
     * 发送Crash请求
     *
     * @param url      请求地址
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public int sendCrash(String url, String token, String filePath) throws IOException {

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }

        java.net.URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        Log.d(TAG, file + " send " + urlObj);
        // 设置请求头信息
        // 获取电话号码
        SharedPreferences sharedPreferences = this.getSharedPreferences("rcs_sp", Context.MODE_MULTI_PROCESS);
        String mobileNo = sharedPreferences.getString("auto_login", "");
        if (TextUtils.isEmpty(mobileNo)) {
            mobileNo = "13922222222";

        } else if (mobileNo.contains("+86")) {
            mobileNo = mobileNo.substring(3);
        }

        if (TextUtils.isEmpty(token)) {
            token = "XXXXXXXXXX";
        }
        Log.e(TAG, "mobileNo=" + mobileNo + "   , token =" + token);
        String publicUserID = String.format("tel:+86%s", "13922222222");
        con.setRequestProperty("X-3GPP-Intended-Identity", "\"" + publicUserID + "\"");
        con.setRequestProperty("Authorization", "UA token=\"" + token + "\"");

        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        // 请求正文信息
        StringBuilder sb = new StringBuilder();
        sb.append("--"); //
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        sb.append("真实手机号码是：" + mobileNo + "\r\n");
        sb.append("当前版本是" + CommonUtils.getVersionName((UploadLogService.this)) + "  " + CommonUtils.getVersionCode(UploadLogService.this));
        sb.append("\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        out.write(head);

        // 文件正文部分
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//

        out.write(foot);

        out.flush();
        out.close();

        // 读取服务器响应，必须读取,否则提交不成功
        Log.e(TAG, "con.getResponseCode()=" + con.getResponseCode());//返回200上传成功
        return con.getResponseCode();
    }

    protected UploadResultModel initLogUpload(String pathString) {
        Log.e(TAG, "path is " + pathString);
        UploadResultModel uploadResultModel = new UploadResultModel();
        if(pathString.equals(LOG_DIR_LOGIN)){
            uploadResultModel.type = UploadType.LOGIN;
        }else if(pathString.equals(LOG_DIR_XLOG)){
            uploadResultModel.type = UploadType.XLOG;
        }else if(pathString.equals(LOG_DIR_GENERAL)){
            uploadResultModel.type = UploadType.GENERAL;
        }else{
            uploadResultModel.type = UploadType.LOGIN;
        }
        File strF = new File(pathString);
        if (!strF.exists()) {
            strF.mkdir();
        }
        uploadResultModel.archiveString = pathString;
        File zipFile = new File(uploadResultModel.archiveString);
        if (!zipFile.exists()) {
            zipFile.mkdir();
            Log.e(TAG, "make zipdir success");
        } else {
            Log.e(TAG, "exit zipdir");
        }
        //获取时间
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int imonth = month + 1;
        int day = ca.get(Calendar.DAY_OF_MONTH);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        uploadResultModel.mZipName = mobile+"."+ year + "." + imonth + "." + day + "." + hour;
        uploadResultModel.fileSrcStrings = new String[]{pathString};
        mZipControl = new ZipFileControl();

        return uploadResultModel;
    }

    /**
     * 创建发送普通log的 intent
     *
     * @return
     */
    public static Intent createUploadLogIntent(Context context, boolean isPS) {
        Intent intent = new Intent(context, UploadLogService.class);
        intent.putExtra(UPLOAD_GENERAL_LOG, true);
        intent.putExtra(UPLOAD_LOG_BY_MOBILE_NETWORK, isPS);
        return intent;
    }

    /**
     * 创建发送异常log的 intent
     *
     * @return
     */
    public static Intent createUploadExceptionIntent(Context context, String uploadFileName, String phone) {
        Intent intent = new Intent(context, UploadLogService.class);
        intent.putExtra(UPLOAD_EXCEPTION_LOG, true);
        intent.putExtra(UPLOAD_LOG_BY_MOBILE_NETWORK, true);
        intent.putExtra(UPLOAD_EXCEPTION_LOG_FILE_NAME, uploadFileName);
        intent.putExtra(UPLOAD_USER_MOBILE, phone);
        return intent;
    }

    /**
     * 创建发送登录log的 intent
     *
     * @return
     */
    public static Intent createUploadLoginIntent(Context context, String phone) {
        Intent intent = new Intent(context, UploadLogService.class);
        intent.putExtra(UPLOAD_LOGIN_LOG, true);
        intent.putExtra(UPLOAD_USER_MOBILE, phone);
        return intent;
    }

    public static String getUploadLogUrl(Context context) {
        String updateLogUrl;
//        boolean isLogUrlopen = (boolean)SharePreferenceUtils.getDBParam(context, "url_sclogUpload_open", false);
        boolean isLogUrlopen = true;
        if(isLogUrlopen) {
            updateLogUrl = (String)SharePreferenceUtils.getDBParam(context,"url_sclogUpload",URL);;
        } else {
            updateLogUrl = "";
        }
        Log.d(TAG,"isLogUrlopen" + isLogUrlopen + ";updateLogUrl: " + updateLogUrl);
        return updateLogUrl;
    }

    /**
     * @作用:递归删除指定时间之前的log文件(登录log和普通log)
     */
    public static void deleteOldLog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("rcs_sp", Context.MODE_MULTI_PROCESS);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.CHINA);
        String lastRemoveDate = sDateFormat.format(new Date(calendar
                .getTimeInMillis()));

        if (!sharedPreferences.getString("lastRemoveLogDay", "").equals(lastRemoveDate)) {
            FileUtil.deleteFilesByTime(calendar.getTimeInMillis() - TWO_DATE_TIME_MILLIS, new File(LOG_DIR_GENERAL), true, "mtc", "mme");
            FileUtil.deleteFilesByTime(calendar.getTimeInMillis() - TWO_DATE_TIME_MILLIS, new File(LOG_DIR_LOGIN), false, "");
            sharedPreferences.edit().putString("lastRemoveLogDay", lastRemoveDate).commit();
        }
    }

    /**
     * 发送上传请求
     *
     * @param url      请求地址
     * @return
     * @throws IOException
     */
    public int sendLogFile(String url, String filePath, String type, String mobile) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }
        return doUploadRequest2(url ,filePath, type, mobile);
    }

    private int doUploadRequest2(String url, String filepath, String type, String mobile)  throws IOException {
        Log.d(TAG, "filepath = " + filepath);
        if (TextUtils.isEmpty(filepath)) {
            return -1;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return -1;
        }
        if(TextUtils.isEmpty(url)){
            url = URL;
        }
        mobile = NumberUtils.getformatPhone(mobile);
        if (!NumberUtils.isPhoneNumber(mobile)) {
            mobile = "";
        }

        int version = CommonUtils.getVersionCode(this);

        // 构造once参数
        String uuid = UUID.randomUUID().toString();
        String subUuid = uuid.substring(uuid.length() - 5 ,uuid.length());
        String timeStr =  Long.toHexString(System.currentTimeMillis());
        String subTime = timeStr.substring(timeStr.length()-7 , timeStr.length());
        String once = subUuid + subTime;

        StringBuilder src = new StringBuilder();
        src.append("app_key=").append(UPLOAD_APP_KEY);
        src.append("mobile=").append(mobile);
        src.append("once=").append(once);
        src.append("sdk_from=").append(UPLOAD_SDK_FROM);
        src.append("type=").append(type);
        src.append("version=").append(version);
        src.append(UPLOAD_APP_SECRET);
        String sha1 = SHA.getSha1(src.toString());

        Log.d(TAG, "src = " + src);
        Log.d(TAG, "sha1 = " + sha1);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        String uploadName = "andfetion.android." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + file.getName();// android_2018_11_22_....
        Log.d(TAG, "uploadName = " + uploadName);
        MultipartBody multipartBody =new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("app_key", UPLOAD_APP_KEY)
                .addFormDataPart("mobile", mobile)
                .addFormDataPart("once", once)
                .addFormDataPart("sdk_from", UPLOAD_SDK_FROM)
                .addFormDataPart("type", type)
                .addFormDataPart("version", String.valueOf(version))
                .addFormDataPart("signature", sha1)
                .addFormDataPart("files", uploadName, RequestBody.create(MediaType.parse("file/*"), file))//添加文件
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = client.newCall(request).execute();
        int code = response.code();
        String responseBody = response.body().string();
        Log.d(TAG, "response code = " + code + " responsebody = " + responseBody);
        if (code != 200) {
            return -1;
        }
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            int c = jsonObject.getInt("code");
            return (c == 0) ? 0 : -1;
        } catch (JSONException e) {
            return -1;
        }

    }

    public static String getUploadLogUrl2(Context context) {
        String uploadLogUrl;
        boolean isLogUrlopen = (boolean) SharePreferenceUtils.getDBParam(context, StringConstant.KEY_FOR_UPLOAD_LOG_OPEN, true);
        if(isLogUrlopen) {
            uploadLogUrl = (String)SharePreferenceUtils.getDBParam(context,StringConstant.KEY_FOR_UPLOAD_LOG, URL);
        } else {
            uploadLogUrl = "";
        }
        Log.d(TAG,"isLogUrlopen" + isLogUrlopen + ";uploadLogUrl: " + uploadLogUrl);
        return uploadLogUrl;
    }
}
