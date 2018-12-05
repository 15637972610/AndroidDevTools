package com.dkp.shopping.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by admin on 2018/6/27.
 */

public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * 应用程序在SDCARD中的根目录
     */
    public static String DIR_PUBLLIC_ROOT = Environment
            .getExternalStorageDirectory() + File.separator + "ZJ";

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
     * 获取文件的MD5值(RandomAccessFile方式)
     * 该方法较耗时，请务必在子线程中使用
     */
    public static String getFileMd5(File file) {
        MessageDigest messageDigest;
        RandomAccessFile randomAccessFile = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            if (file == null) {
                return "";
            }
            if (!file.exists()) {
                return "";
            }
            randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[1024 * 1024 * 10];
            int len = 0;
            while ((len = randomAccessFile.read(bytes)) != -1) {
                messageDigest.update(bytes, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, messageDigest.digest());
            StringBuilder md5 = new StringBuilder(bigInt.toString(16));
            while (md5.length() < 32) {
                md5.insert(0, "0");
            }
            return md5.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                    randomAccessFile = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * sdcard是否可读写
     *
     * @return
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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
     * 创建文件及其父目录
     *
     * @param file
     * @return
     */
    public static boolean createNewFileAndParentDir(File file) {
        boolean isCreateNewFileOk = true;
        isCreateNewFileOk = createParentDir(file);
        // 创建父目录失败，直接返回false，不再创建子文件
        if (isCreateNewFileOk) {
            if (!file.exists()) {
                try {
                    isCreateNewFileOk = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    isCreateNewFileOk = false;
                }
            }
        }
        return isCreateNewFileOk;
    }

    /**
     * 创建文件父目录
     *
     * @param file
     * @return
     */
    public static boolean createParentDir(File file) {
        boolean isMkdirs = true;
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                isMkdirs = dir.mkdirs();
            }
        }
        return isMkdirs;
    }

}
