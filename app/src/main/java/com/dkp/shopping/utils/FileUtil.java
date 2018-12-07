package com.dkp.shopping.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 工具类，文件相关操作
 */
public final class FileUtil {
    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
    public static final int MAX_RECORD_TIME = 180 * 1000; // ms
    private final static String TAG = "FileUtil";

    /**
     * 存储路径方式
     */
    public enum PathType {
        /**
         * 存储在SDCard中
         */
        SDCARD,
        /**
         * 存储在软件Cache中
         */
        CACHE,
        /**
         * 存储在软件DATA目录下的app_photos中
         */
        APP_PHOTOS,
        /**
         * 存储在多媒体文件夹下
         */
        SDCARD_PHOTOS,
        /**
         * 存储在多媒体文件夹下
         */
        MEDIA_DIR,
        /**
         * 存储在软件DATA目录下的app_xmls中
         */
        APP_XML
    }

    public static final String R_DATA_DIRECTORY = "r_data_directory";
    public static final String R_DATA_DIRECTORY_KEY = "r_data_directory_key";

    public static final String R_DATA_DIRECTORY_EXTERNAL = "external";
    public static final String R_DATA_DIRECTORY_DATA = "data";

    private static String R_DATA_DIR;
    private static String R_PROFILE_DIR;
    private static String R_LOG_DIR;
    private static String R_LOGIN_LOG_DIR;
    private static String R_CRASH_DIR;
    private static String R_DOWNLOAD_DIR;
    private static String R_TEMP_FILE_DIR;
    private static String R_THUMBNAIL_DIR;
    private static String R_THUMBNAIL_DOWNLOAD_DIR;
    private static String R_PHOTO_DIR;
    private static String R_FILE_DIR;
    private static String R_CLIENT_DIR;

    /**
     * 客户端发帖最大照片文件大小，30kb，如果大于，就需要压缩
     */
    public static final int MAX_POST_PHOTO_FILE_SIZE = 30720;

    /**
     * 图片最大边像素
     */
    public static final int MAX_POST_PHOTO_PX = 600;

    /**
     * 图片质量
     */
    public static final int PHOTO_QUANLITY_CAMER = 80;
    public static final int PHOTO_QUANLITY_META = 50;
    public static final int PHOTO_QUANLITY_SMALLMETA = 70;
    /**
     * 图片格式
     */
    public static final String PHOTO_FORMATE = "jpg";

    /**
     * 照片子路径
     */
    public static final String PHOTO_DIR = "photos";

    /**
     * XML文件路径
     */
    public static final String XML_DIR = "xmls";

    /**
     * Image jpg文件后缀
     */
    public static final String EXE_JPG = "jpg";

    public static String LOG_DIR_GENERAL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/log";
    public static String LOG_DIR_LOGIN = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/login_log";
    public static String LOG_DIR_XLOG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/xlog/log";
    public static String LOG_DIR_CRASH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/log/crash";

    public static String DIR_PUBLLIC_ROOT = Environment
            .getExternalStorageDirectory() + "/dkpdemo";

    public static String DIR_DOWNLOAD_UPDATE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/update";
    // 名片夹的路劲
    public static String DIR_DOWNLOAD_CARDCLIP = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dkpdemo/cardClip";

    public static String DIR_MESSAGE_AUDIO = File.separator + "audio";

    public static String DIR_IMAGE_PORTRAIT = File.separator + "group_portrait";

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断文件是否存在
     *
     * @param strFile
     * @return 是否存在
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * 获得文件的路径，如果有SD卡，那么首先获得SD卡的路径
     *
     * @param context
     *            上下文
     * @param dir
     *            子目录名
     * @return 子目录的绝对路径
     */
    /**
     * @param context
     * @param dir
     * @return
     */
    public static String getPathSDCardFirst(Context context, String dir) {
        String absolutePath = "";
        if (sdcardAvailable()) {
            absolutePath = createSavePath(context, PathType.SDCARD);
            absolutePath += File.separator + "dkpdemo" + File.separator + dir;
        } else {
            absolutePath = context.getDir(dir, Context.MODE_PRIVATE).getPath();
        }
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return absolutePath;
    }


    /**
     * 获取gifs文件夹路径
     *
     * @return gif文件存放绝对路径
     */
    public static String getGifsPathUnderData(Context context) {

        File file = context.getExternalFilesDir(null);
        String receiveGifCachePath = "receiveGif";
        File cacheFile = new File(file, receiveGifCachePath);
        if (cacheFile != null && !cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }

    /**
     * 拷贝文件 将文件A拷贝到文件B fileChannel方式
     *
     * @param fileNameA 原文件
     * @param fileNameB 目标文件
     * @return 是否拷贝成功
     */
    public static boolean copyTo(String fileNameA, String fileNameB) {
        File s = new File(fileNameA);
        File t = new File(fileNameB);
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        boolean result = false;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (in != null)
                    in.close();
                if (fo != null)
                    fo.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断是否有SD卡
     *
     * @return 是否有SD卡
     */
    public static boolean sdcardAvailable() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得文件的路径
     *
     * @param context  上下文
     * @param pathType 路径类型
     * @return 特定路径类型的路径
     */
    public static String createSavePath(Context context, PathType pathType) {
        String path;
        switch (pathType) {
            case CACHE:
                path = context.getCacheDir().getPath();
                break;
            case APP_PHOTOS:
                path = context.getDir(PHOTO_DIR, Context.MODE_WORLD_WRITEABLE).getPath();
                break;
            case APP_XML:
                path = context.getDir(XML_DIR, Context.MODE_WORLD_WRITEABLE).getPath();
                break;
            case SDCARD:
                path = Environment.getExternalStorageDirectory().getPath();
                break;
            case SDCARD_PHOTOS:
                path = Environment.getExternalStorageDirectory().getPath() + "/" + PHOTO_DIR;
                File fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                break;
            default:
                path = Environment.getExternalStorageDirectory().getPath();
                break;
        }
        return path;
    }

    /**
     * 创建指定路径的文件夹
     *
     * @param path 路径
     * @return 通过路径创建的File对象
     * @throws SecurityException 安全异常
     */
    public static File createDir(String path) throws SecurityException {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    /**
     * 创建文件 若该文件存在删除该文件再创建，若不存在则创建
     *
     * @param file 需要创建的文件
     * @return 是否创建成功
     * @throws IOException IO异常
     */
    public static boolean createFile(File file) throws IOException {
        if (file.exists()) {
            deleteFile(file);
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return file.createNewFile();
    }

    /**
     * 文件Uri来自于Media Provider
     */
    public static final int FROM_TYPE_MEDIA = 1;

    /**
     * 文件Uri来自于存储设备或是指定的存储路径
     */
    public static final int FROM_TYPE_STORE = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * 从文件Uri获取该文件的容量大小
     *
     * @param context  上下文
     * @param fileUri  文件Uri
     * @param fromType 类型
     * @return 对应文件大小
     */
    public static long getFileSize(Context context, Uri fileUri, int fromType) {
        long fileSizeinByte = 0;
        String mediaFilePath = null;
        switch (fromType) {
            case FROM_TYPE_MEDIA:
                mediaFilePath = getMediaRealPath(context, fileUri);
                break;
            case FROM_TYPE_STORE:
                mediaFilePath = fileUri.getEncodedPath();
                break;
            default:
                break;
        }
        try {
            if (mediaFilePath != null) {
                File mediaFile = new File(mediaFilePath);
                fileSizeinByte = mediaFile.length();
            }
        } catch (Exception e) {
        }
        return fileSizeinByte;
    }

    /**
     * 获得指定文件的大小
     *
     * @param filePath 文件路径
     * @return 对应文件大小
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        long length = file.length();
        return length;
    }

    /**
     * 获得指定文件的大小
     *
     * @param file 文件
     * @return 对应文件大小
     */
    public static double getFileSize(File file, int sizeType) {
        long fileS = file.length();
        return FormetFileSize(fileS, sizeType);
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取媒体文件真实文件路径
     *
     * @param context    上下文
     * @param contentUri 媒体文件Uri
     * @return 媒体文件Uri对应的真是路径
     */
    public static String getMediaRealPath(Context context, Uri contentUri) {
        if (contentUri == null)
            return null;
        String schema = contentUri.getScheme();
        if (StringUtil.isEmpty(schema))
            return null;
        if ("content".equalsIgnoreCase(schema)) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst())
                    return cursor.getString(column_index);
            } finally {
                if (cursor != null)
                    cursor.close();
                cursor = null;
            }
        } else if ("file".equalsIgnoreCase(schema)) {
            String fileName = contentUri.toString().replace("file://", "");
            return fileName;
        }
        Log.d(TAG, "Uri Scheme:" + schema);
        return null;
    }

    /**
     * 将源文件拷贝到目标文件 传统流拷贝方式
     *
     * @param src 原文件
     * @param dst 目标文件
     * @return 是否拷贝成功
     */
    public static boolean copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            if (!dst.exists()) {
                dst.createNewFile();
            }
            OutputStream out = new FileOutputStream(dst);
            StreamUtil.copyStream(in, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从文件的Uri(带有file://开头的Uri)删除该文件
     *
     * @param fileUri 要删除的文件Uri
     * @return 是否删除成功
     */
    public static final boolean deleteFile(Uri fileUri) {
        File tempFile = new File(fileUri.getEncodedPath());
        return deleteFile(tempFile);
    }

    /**
     * 删除指定文件
     *
     * @param file 需要删除的文件
     * @return
     */
    public static final boolean deleteFile(File file) {
        boolean result = true;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    result &= deleteFile(children[i]);
                }
            }
        }
        result &= file.delete();
        return result;
    }

    /**
     * 根据文件的最后修改时间来删除旧的文件，解决可能出现空指针的问题
     *
     * @param dirPath  需要删除的文件夹路径
     * @param fileNums 需要删除的文件个数
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void deleOldFile(String dirPath, int fileNums) throws Exception {
        File file = new File(dirPath);
        File[] listFiles = file.listFiles();
        if (listFiles == null || listFiles.length < 1) {
            return;
        }
        ArrayList<File> list = new ArrayList<File>();
        for (File file0 : listFiles) {
            list.add(file0);
        }

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return 1;
                } else if (((File) o1).lastModified() == ((File) o2).lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        while (list.size() > fileNums) {
            File f = list.get(0);
            if (f.exists()) {
                f.delete();
            }
            list.remove(0);
        }
    }

    /**
     * 删除2天之前的文件，如果文件夹为空，则删除之
     *
     * @param dir 需要删除的文件夹路径
     */
    public static void deleteOldFile(File dir) {
        deleteOldFile(dir, 0);
    }

    private static void deleteOldFile(File dir, int depth) {
        try {
            long now = System.currentTimeMillis();
            long expire = 2 * 24 * 3600 * 1000; // 2天
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    if (f.isDirectory() && depth < 10) {
                        deleteOldFile(f, depth + 1);
                    } else if ((now - f.lastModified()) > expire) {
                        f.delete();
                    }
                }
                // 如果文件夹已经为空，删除
                if (listFiles.length == 0) {
                    dir.delete();
                }
            }
        } catch (Throwable e) {

        }
    }

    /**
     * 移除文件
     *
     * @param filePathName 指定文件名
     * @throws FileNotFoundException 未找到文件异常
     * @throws IOException           IO异常
     */
    public static void remmoveFile(String filePathName) throws FileNotFoundException, IOException {
        File f = new File(filePathName);
        if (f.exists()) {
            FileUtil.deletE(f);
        }
    }

    /**
     * 删除文件夹或文件下面的文件
     *
     * @param f 需要删除的文件或文件夹
     */
    public static void deletE(File f) {
        File[] ff;
        int length;
        if (f.isFile()) {
            f.delete();
            return;
        } else if (f.isDirectory()) {
            ff = f.listFiles();
            if (ff != null) {
                length = ff.length;
                int i = 0;
                while (length != 0) {
                    deletE(ff[i]);
                    i++;
                    length--;
                }
                if (length == 0) {
                    f.delete();
                    return;
                }
            }
        }
    }

    /**
     * @param TimeInMillis    指定时间的时间戳，最好不要为当前获取的时间
     * @param f               需要清理子文件的文件夹或需要删除的文件
     * @param haveIgnore      是否有要忽略都文件
     * @param ignoreFilsNames 要忽略文件的开头字符串,haveIgnore为true时有效
     * @作用: 删除最后编辑时间在指定时间之前的文件或文件夹下面的文件(不包括文件夹)
     * @author: zcc
     */
    public static void deleteFilesByTime(final Long TimeInMillis, File f, final boolean haveIgnore, String... ignoreFilsNames) {
        if (TimeInMillis == null || f == null || ignoreFilsNames == null) {
            Log.d(TAG, "deleteFilesByTime() 参数为空！！！");
            return;
        }

        File[] ff;
        int length;
        if (f.isFile()) {
            if (f.lastModified() < TimeInMillis) {
                boolean isIgnoreFile = false;
                if (haveIgnore) {
                    for (int i = 0; i < ignoreFilsNames.length; i++) {
                        if (f.getName().startsWith(ignoreFilsNames[i])) {
                            isIgnoreFile = true;
                            break;
                        }
                    }
                } else {
                    isIgnoreFile = true;
                }
                if (!isIgnoreFile) {
                    Log.e(TAG, "delete file -->file.lastModified()=" + f.lastModified() + "TimeInMillis=" + TimeInMillis + ",file.getName()=" + f.getName());
                    f.delete();
                }
            }
            return;
        } else if (f.isDirectory()) {
            ff = f.listFiles();
            if (ff != null) {
                length = ff.length;
                int i = 0;
                while (length != 0) {
                    deleteFilesByTime(TimeInMillis, ff[i], haveIgnore, ignoreFilsNames);
                    i++;
                    length--;
                }
            }
        }
    }

    /**
     * 获取数据文件夹路径
     *
     * @param context 上下文
     * @return 数据文件夹路径
     */
    public static String getDataDir(Context context) {
        String dir = null;
        SharedPreferences sp = context.getSharedPreferences(R_DATA_DIRECTORY, Context.MODE_PRIVATE);
        String fstDir = sp.getString(R_DATA_DIRECTORY_KEY, "");
        if (fstDir.equals(R_DATA_DIRECTORY_EXTERNAL)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if (fstDir.equals(R_DATA_DIRECTORY_DATA)) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
            sp.edit().putString(R_DATA_DIRECTORY_KEY, R_DATA_DIRECTORY_DATA).commit();
        }
        return dir;
    }

    /**
     * 初始化SDK文件夹
     *
     * @param context    上下文
     * @param sdcardPath sdcard路径
     */
    public static void initSDKDirs(Context context, String sdcardPath, String appName) {
        Log.d("TAG", "initSDKDirs");
        R_DATA_DIR = getDataDir(context) + "/" + appName;
        String sdPath = sdcardPath;
        if (TextUtils.isEmpty(sdcardPath)) {
            sdPath = R_DATA_DIR;
        }
        // init /data/data
        File file = new File(R_DATA_DIR);
        file.mkdirs();
        R_PROFILE_DIR = R_DATA_DIR + "/profiles";
        file = new File(R_PROFILE_DIR);
        file.mkdirs();

        // init sdcardPath
        file = new File(sdPath);
        R_LOG_DIR = sdPath + "/log";
        file = new File(R_LOG_DIR);
        file.mkdirs();
        R_LOGIN_LOG_DIR = sdPath + "/login_log";
        file = new File(R_LOGIN_LOG_DIR);
        file.mkdirs();
        R_CRASH_DIR = R_LOG_DIR + "/crash";
        file = new File(R_CRASH_DIR);
        file.mkdirs();
        R_DOWNLOAD_DIR = sdPath + "/download";
        file = new File(R_DOWNLOAD_DIR);
        file.mkdirs();
        R_TEMP_FILE_DIR = sdPath + "/temp";
        file = new File(R_TEMP_FILE_DIR);
        file.mkdirs();
        R_THUMBNAIL_DIR = R_TEMP_FILE_DIR + "/.thumbnails";
        file = new File(R_THUMBNAIL_DIR);
        file.mkdirs();
//        R_THUMBNAIL_DOWNLOAD_DIR = RCS_THUMBNAIL_DIR + "/download";
//        file = new File(R_THUMBNAIL_DOWNLOAD_DIR);
//        file.mkdirs();
        R_PHOTO_DIR = R_DOWNLOAD_DIR + "/photos";
        file = new File(R_PHOTO_DIR);
        file.mkdirs();
        R_FILE_DIR = R_DOWNLOAD_DIR + "/files";
        file = new File(R_FILE_DIR);
        file.mkdirs();
        R_CLIENT_DIR = sdPath + "/client";
        file = new File(R_CLIENT_DIR);
        file.mkdirs();
        getGifsPathUnderData(context);
    }

    /**
     * 获得保存目录
     *
     * @return 返回保存目录
     */
    public static String getSaveDir() {
        return R_DOWNLOAD_DIR;
    }

    /**
     * 获得下载目录
     *
     * @return 返回下载目录
     */
    public static String getDownloadDir() {
//        return R_THUMBNAIL_DOWNLOAD_DIR;
        return R_FILE_DIR;
    }

    /**
     * 获得Client目录
     *
     * @return 返回Client目录
     */
    public static String getClientDir() {
        return R_CLIENT_DIR;
    }

    /**
     * Get directory of SDK logs file. 获得SDK日志目录
     *
     * @return SDK logs directory. 返回SDK日志目录
     */
    public static String getSDKLogDir() {
        return R_LOG_DIR;
    }

    /**
     * Get directory of SDK profiles file. 获得SDKprofile目录
     *
     * @return SDK profiles directory. 返回SDKprofile目录
     */
    public static String getSDKProfilesDir() {
        return R_PROFILE_DIR;
    }

    /**
     * Get directory of crash file. 获得崩溃日志目录
     *
     * @return crash directory. 返回崩溃日志目录
     */
    public static String getCrashDir() {
        return R_CRASH_DIR;
    }

    /**
     * 获得预览图文件存放目录
     *
     * @return 返回预览图文件存放目录
     */
    public static String getThumbnailDir() {
        return R_THUMBNAIL_DIR;
    }

    /**
     * 获得预览图文件存放目录
     *
     * @return 返回预览图文件存放目录
     */
    public static String getPhotoDir() {
        return R_PHOTO_DIR;
    }

    /**
     * @return 临时目录
     */
    public static String getTempDir() {
        return R_TEMP_FILE_DIR;
    }

    /**
     * 获得文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名
     */
    public static String getFilePostfix(String filePath) {
        int tmp = filePath.lastIndexOf(".");
        String postfix = "";
        try {
            postfix = filePath.substring(tmp, filePath.length());
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return postfix;
    }

    /**
     * 获得文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        int tmp = filePath.lastIndexOf(File.separatorChar);
        String fileName = filePath.substring(tmp + 1);
        return fileName;
    }

    /**
     * 读取文件内容到字符串
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        return new String(readFileToBytes(new File(filePath)));
    }

    /**
     * 读取文件内容到字节数组
     *
     * @param file
     * @return
     */
    public static byte[] readFileToBytes(File file) {
        byte[] bytes = null;
        if (file.exists()) {
            byte[] buffer = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            ByteArrayOutputStream baos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                baos = new ByteArrayOutputStream();
                bos = new BufferedOutputStream(baos, DEFAULT_BUFFER_SIZE);
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len = 0;
                while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();
                bytes = baos.toByteArray();
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                        bos = null;
                    }
                    if (baos != null) {
                        baos.close();
                        baos = null;
                    }
                    if (bis != null) {
                        bis.close();
                        bis = null;
                    }
                    buffer = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.v(TAG, "readFileToBytes.file = " + file + ", bytes.length = " + (bytes == null ? 0 : bytes.length));
        return bytes;
    }


    /**
     * 写字节数组到文件，文件父目录如果不存在，会自动创建
     *
     * @param filePath
     * @param bytes
     * @return
     */
    public static boolean writeBytesToFile(String filePath, byte[] bytes) {
        return writeBytesToFile(filePath, bytes, false);
    }

    /**
     * 写字节数组到文件，文件父目录如果不存在，会自动创建
     *
     * @param filePath
     * @param bytes
     * @param isAppend
     * @return
     */
    public static boolean writeBytesToFile(String filePath, byte[] bytes, boolean isAppend) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);

        boolean isWriteOk = false;
        byte[] buffer = null;
        int count = 0;
        ByteArrayInputStream bais = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                bos = new BufferedOutputStream(new FileOutputStream(file, isAppend), DEFAULT_BUFFER_SIZE);
                bais = new ByteArrayInputStream(bytes);
                bis = new BufferedInputStream(bais, DEFAULT_BUFFER_SIZE);
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len = 0;
                while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, len);
                    count += len;
                }
                bos.flush();
            }
            isWriteOk = bytes.length == count;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                    bos = null;
                }
                if (bis != null) {
                    bis.close();
                    bis = null;
                }
                if (bais != null) {
                    bais.close();
                    bais = null;
                }
                buffer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "writeByteArrayToFile.file = " + file + ", bytes.length = " + (bytes == null ? 0 : bytes.length) + ", isAppend = " + isAppend + ", isWriteOk = " + isWriteOk);
        return isWriteOk;
    }

    /**
     * 创建文件父目录
     *
     * @param file
     * @return 是否创建成功
     */
    public static boolean createParentDir(File file) {
        boolean isMkdirs = true;
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                isMkdirs = dir.mkdirs();
                Log.v(TAG, "createParentDir.dir = " + dir + ", isMkdirs = " + isMkdirs);
            }
        }
        return isMkdirs;
    }

    /**
     * 创建文件及其父目录
     *
     * @param file
     * @return
     */
    public static boolean createNewFileAndParentDir(File file) {
        boolean isCreateNewFileOk = true;
        isCreateNewFileOk = createParentDir(file);
        //创建父目录失败，直接返回false，不再创建子文件
        if (isCreateNewFileOk) {
            if (!file.exists()) {
                try {
                    isCreateNewFileOk = file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    isCreateNewFileOk = false;
                }
            }
        }
        Log.d(TAG, "createFileAndParentDir.file = " + file + ", isCreateNewFileOk = " + isCreateNewFileOk);
        return isCreateNewFileOk;
    }

    /**
     * 获得缩略图路径
     *
     * @param fileName 文件名
     * @return 对应的缩略图路径
     */
    public static String createThumbnailPath(String fileName) {
        String path = getThumbnailDir() + "/thumb_" + fileName;
        return path;
    }

    /**
     * 获得指定文件名对应下载文件路径，若该文件存在则重命名为文件名＋（count）＋扩展名形式
     *
     * @param fileName 指定文件名
     * @return 对应的下载文件路径
     * @deprecated
     */
    public static String syncFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        String filePath = getDownloadDir() + "/" + fileName;
        if (!fileName.contains(".")) {
            int count = 1;
            while (true) {
                File file = new File(filePath);
                if (!file.exists()) {
                    break;
                }
                fileName = fileName + "(" + count + ")";
                filePath = getDownloadDir() + "/" + fileName;
                count++;
            }
        } else {
            int tmp = fileName.lastIndexOf(".");
            String end = fileName.substring(tmp, fileName.length());
            String start = fileName.substring(0, tmp);
            int count = 1;
            while (true) {
                File file = new File(filePath);
                if (!file.exists()) {
                    break;
                }
                fileName = start + "(" + count + ")" + end;
                filePath = getDownloadDir() + "/" + fileName;
                count++;
            }
        }
        return fileName;
    }

    /**
     * 根据tsfId修改文件名
     *
     * @param fileName
     * @param dwTsfId
     * @return
     */
    public static String syncFileName(String fileName, int dwTsfId) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        if (!fileName.contains(".")) {
            fileName = fileName + "(" + dwTsfId + ")";
        } else {
            int tmp = fileName.lastIndexOf(".");
            String end = fileName.substring(tmp, fileName.length());
            String start = fileName.substring(0, tmp);
            fileName = start + "(" + dwTsfId + ")" + end;
        }
        return fileName;
    }

    public static String createDownloadFilePath(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return null;
        String filePath = getThumbnailDir() + "/" + fileName;
        return filePath;
    }

    /**
     * 通过制定文件名创建下载文件路径，若该文件存在则重命名为文件名＋（count）＋扩展名形式
     *
     * @param fileName 指定文件名
     * @return 对应下载文件绝对路径
     */
    public static String createDownloadFilePath(String fileName, int dwTsfId) {
        if (TextUtils.isEmpty(fileName))
            return null;

        String filePath;
        filePath = getThumbnailDir() + "/" + fileName;

        if (!fileName.contains(".")) {
            filePath = filePath + "(" + dwTsfId + ")";
        } else {
            int tmp = filePath.lastIndexOf(".");
            String end = filePath.substring(tmp, filePath.length());
            String start = filePath.substring(0, tmp);
            filePath = start + "(" + dwTsfId + ")" + end;
        }
        return filePath;
    }

    public static String createSaveFilePath(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return null;

        int tmp = fileName.lastIndexOf("/");
        String end = fileName.substring(tmp+1, fileName.length());

        String filePath;
        filePath = getDownloadDir() + "/" + end;

        return filePath;
    }


    /**
     * 获得缩略图路径
     *
     * @param fileName 文件名
     * @return 对应的缩略图路径
     */
    public static String createPhotoPath(String fileName) {
        String path = getPhotoDir() + "/photo_" + fileName;
        return path;
    }

    /**
     * 获得指定视频文件的时长
     *
     * @param path 视频文件路径
     * @return 时长（毫秒）
     */
    public static long getDuring(String path) {
        int seconds = 0;
        MediaPlayer mMediaPlayer = new MediaPlayer();
        FileInputStream fileInputStream = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
                FileDescriptor fd = fileInputStream.getFD();
                mMediaPlayer.setDataSource(fd);
                mMediaPlayer.prepare();
                seconds = mMediaPlayer.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
            seconds = 0;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        Log.e(TAG, "seconds=" + seconds);
        Log.i("FileUtil", "% seconds=" + seconds % 1000);
        if (seconds % 1000 >800) {
            Log.i("FileUtil", "% seconds=" + seconds % 1000);
            seconds += 1000;
        }

        if (seconds > MAX_RECORD_TIME) {
            seconds = MAX_RECORD_TIME;
        }
        return seconds;
    }

    public static Object invokeMethod(Object receiver, String methodName, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Class[] paramsType = params == null ? null : new Class[params.length];
        if (paramsType != null) {
            for (int a = 0; a < params.length; a++) {
                if (params[a] != null) {
                    paramsType[a] = params[a].getClass();
                }
            }
        }
        return receiver.getClass().getMethod(methodName, paramsType).invoke(receiver, params);
    }

    public static Object invokeMethod(Object receiver, String methodName, Class[] paramsType, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return receiver.getClass().getMethod(methodName, paramsType).invoke(receiver, params);
    }

    public static int getFileResumeRepeatTimes(int filesize) {
        return (int) ((filesize / (1024 * 1024 * 50)) * 4 + 5);
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);

            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {

                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 写字符串到文件，文件父目录如果不存在，会自动创建
     *
     * @param file
     * @param content
     * @param isAppend
     * @return
     */
    public static boolean writeStringToFile(File file, String content, boolean isAppend) {
        boolean isWriteOk = false;
        char[] buffer = null;
        int count = 0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                br = new BufferedReader(new StringReader(content));
                bw = new BufferedWriter(new FileWriter(file, isAppend));
                buffer = new char[DEFAULT_BUFFER_SIZE];
                int len = 0;
                while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bw.write(buffer, 0, len);
                    count += len;
                }
                bw.flush();
            }
            isWriteOk = content.length() == count;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
                buffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    /**
     * 创建目录
     *
     * @param fileName
     * @return
     */
    public static String createDirPath(String fileName) {
        File file = new File(fileName);
        if (file.exists() || file.mkdirs()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String createAudioDir(String parentFileName) {
        return createDirPath(DIR_PUBLLIC_ROOT + File.separator + parentFileName
                + DIR_MESSAGE_AUDIO);
    }

    /**
     * 创建音频文件夹
     *
     * @param parentFilePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createAudioFile(String parentFilePath, String fileName)
            throws IOException {
        File file = new File(parentFilePath + File.separator + fileName
                + ".amr");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return null;
            }
        }
        return file;
    }

    public static void saveGroupPhoto(String key, Bitmap bitmap) {
        File dirFile = new File(DIR_PUBLLIC_ROOT + File.separator + DIR_IMAGE_PORTRAIT);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(DIR_PUBLLIC_ROOT + File.separator + DIR_IMAGE_PORTRAIT + File.separator + key);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGroupPhoto(String key) {
        File file = new File(DIR_PUBLLIC_ROOT + File.separator + DIR_IMAGE_PORTRAIT + File.separator + key);
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getGroupBitmapFromDiskCache(String key) {
        Bitmap mBitmap = null;
        String filePath = DIR_PUBLLIC_ROOT + File.separator + DIR_IMAGE_PORTRAIT + File.separator + key;
        File file = new File(filePath);
        if (file.exists() && file.isFile() && file.length() > 0) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                FileInputStream fis = new FileInputStream(file);
                mBitmap = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mBitmap;
    }

    public static String getDefaultGifDIR(Context context) {

        File file = context.getExternalFilesDir(null);
        String receiveGifCachePath = "receiveGif/defaultGif";
        File cacheFile = new File(file, receiveGifCachePath);
        if (cacheFile != null && !cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }


    public static void copyDataBaseToSD(Context context, String dbname){
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return ;
        }
        String filePath = Environment.getExternalStorageDirectory() + "/dkpdemo/db/";
        if(!fileIsExists(filePath)) {
            createDir(filePath);
        }
        File dbFile = new File(context.getDatabasePath(dbname)+".db");
        File file  = new File(filePath, "r_copy.db");

        FileChannel inChannel = null,outChannel = null;
        try {
            file.createNewFile();
            inChannel = new FileInputStream(dbFile).getChannel();
            outChannel = new FileOutputStream(file).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if(outChannel != null){
                    outChannel.close();
                    outChannel = null;
                }
                Toast.makeText(context, "copy db file to " + Environment.getExternalStorageDirectory() + "/dkpdemo/db/r_copy.db", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写字符串到文件，文件父目录如果不存在，会自动创建
     * @param file
     * @param content
     * @param isAppend
     * @return
     */
    public static boolean writeStringToFileImpl(File file, String content, boolean isAppend) {
        boolean isWriteOk = false;
        char[] buffer;
        int count = 0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                br = new BufferedReader(new StringReader(content));
                bw = new BufferedWriter(new FileWriter(file, isAppend));
                buffer = new char[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bw.write(buffer, 0, len);
                    count += len;
                }
                bw.flush();
            }
            isWriteOk = content.length() == count;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
                buffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    /**
     * sdcard是否可读写
     *
     * @return
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getFileMD5(File file) {

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            return MD5(ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // 关闭流产生的错误一般都可以忽略
                }
            }
        }

    }

    private static String MD5(ByteBuffer buffer) {
        final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        String s = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>>,
                // 逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 获取名片文件夹
     * @return
     */
    public static File getCardClip(Context context ){

        File cardClip = new File(DIR_DOWNLOAD_CARDCLIP);
        if(!cardClip.exists()){
            cardClip.mkdirs();
        }
        return cardClip ;
    }

}
