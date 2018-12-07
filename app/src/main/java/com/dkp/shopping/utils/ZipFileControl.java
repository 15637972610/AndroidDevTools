package com.dkp.shopping.utils;



import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipFileControl {
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    private static boolean isCreateSrcDir = false;//是否创建源目录 在这里的话需要说明下。如果需要创建源目录的话。就在这里设为true否则为false;
    private static String TAG = "ZipControl";
    private String zipFileName = "";

    /**
     * [对指定路径下文件的压缩处理]<BR>
     * [功能详细描述]
     *
     * @param src     径地址
     * @param archive 指定到压缩文件夹的路径
     * @param comment 描述
     * @throws FileNotFoundException 文件没有找到异常
     * @throws IOException           IO输入异常
     */
    public void writeByApacheZipOutputStream(String[] src, String archive,
                                             String comment, String filename) throws FileNotFoundException, IOException {
//        Log.e(TAG, "writeByApacheZipOutputStream  , zipFileName = " + filename);
        zipFileName = filename;
        // ----压缩文件：
        FileOutputStream f = new FileOutputStream(archive);
        // 使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        // 支持中文
        zos.setEncoding("GBK");
        BufferedOutputStream out = new BufferedOutputStream(zos);
        // 设置压缩包注释
        zos.setComment(comment);
        // 启用压缩
        zos.setMethod(ZipOutputStream.DEFLATED);
        // 压缩级别为最强压缩，但时间要花得多一点
        zos.setLevel(Deflater.BEST_COMPRESSION);
        // 如果为单个文件的压缩在这里修改
        for (int i = 0; i < src.length; i++) {
            Log.e(TAG, "src[" + i + "] is " + src[i]);
            File srcFile = new File(src[i]);
            if (!srcFile.exists()
                    || (srcFile.isDirectory() && srcFile.list().length == 0)) {
                Log.e(TAG, "!srcFile.exists()");
                throw new FileNotFoundException(
                        "File must exist and ZIP file must have at least one entry.");
            }
            String strSrcString = src[i];
            // 获取压缩源所在父目录
            strSrcString = strSrcString.replaceAll("////", "/");
            String prefixDir = null;
            if (srcFile.isFile()) {
                prefixDir = strSrcString.substring(0,
                        strSrcString.lastIndexOf("/") + 1);
            } else {
                prefixDir = (strSrcString.replaceAll("/$", "") + "/");
            }
            // 如果不是根目录
            if (prefixDir.indexOf("/") != (prefixDir.length() - 1)
                    && isCreateSrcDir) {
                prefixDir = prefixDir.replaceAll("[^/]+/$", "");
            }
            // 开始压缩
            writeRecursive(zos, out, srcFile, prefixDir);
        }

        out.close();
        // 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
        Log.e(TAG, "Checksum: " + csum.getChecksum().getValue());
        @SuppressWarnings("unused")
        BufferedInputStream bi;
    }

    /**
     * [* 使用 org.apache.tools.zip.ZipFile 解压文件，它与 java 类库中的
     * java.util.zip.ZipFile 使用方式是一新的，只不过多了设置编码方式的 接口。
     * <p>
     * 注，apache 没有提供 ZipInputStream 类，所以只能使用它提供的ZipFile 来读取压缩文件。]<BR>
     *
     * @param archive       压缩包路径
     * @param decompressDir 解压路径
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ZipException
     */

    @SuppressWarnings("unchecked")
    public static void readByApacheZipFile(String archive, String decompressDir)
            throws IOException, FileNotFoundException, ZipException {
        Log.e(TAG, "readByApacheZipFile");
        BufferedInputStream bi;
        ZipFile zf = new ZipFile(archive, "GBK");//支持中文
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory()) {
                Log.e(TAG, "正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                Log.e(TAG, "正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
    }

    /**
     * [使用 java api 中的 ZipInputStream 类解压文件，但如果压缩时采用了
     * org.apache.tools.zip.ZipOutputStream时，而不是 java 类库中的
     * java.util.zip.ZipOutputStream时，该方法不能使用，原因就是编码方 式不一致导致，运行时会抛如下异常：
     * java.lang.IllegalArgumentException at
     * java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:290)
     * <p>
     * 当然，如果压缩包使用的是java类库的java.util.zip.ZipOutputStream 压缩而成是不会有问题的，但它不支持中文 ]<BR>
     * [功能详细描述]
     *
     * @param archive       压缩包路径
     * @param decompressDir 解压路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void readByZipInputStream(String archive, String decompressDir)
            throws FileNotFoundException, IOException {
        BufferedInputStream bi;
        //----解压文件(ZIP文件的解压缩实质上就是从输入流中读取数据):
        Log.e(TAG, "开始读压缩文件");
        FileInputStream fi = new FileInputStream(archive);
        CheckedInputStream csumi = new CheckedInputStream(fi, new CRC32());
        ZipInputStream in2 = new ZipInputStream(csumi);
        bi = new BufferedInputStream(in2);
        java.util.zip.ZipEntry ze;//压缩文件条目
        //遍历压缩包中的文件条目
        while ((ze = in2.getNextEntry()) != null) {
            String entryName = ze.getName();
            if (ze.isDirectory()) {
                Log.e(TAG, "正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(decompressDir + "/"
                        + entryName);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                Log.e(TAG, "正在创建解压文件 - " + entryName);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(decompressDir
                                + "/"
                                + entryName.substring(entryName.lastIndexOf("//"),
                                entryName.length()
                                        - (entryName.lastIndexOf("//") - 2))));
                byte[] buffer = new byte[1024];
                int readCount = bi.read(buffer);
                while (readCount != -1) {
                    bos.write(buffer, 0, readCount);
                    readCount = bi.read(buffer);
                }
                bos.close();
            }
        }
        bi.close();
        Log.e(TAG, "Checksum: " + csumi.getChecksum().getValue());
    }

    /**
     * [递归压缩
     * <p>
     * 使用 org.apache.tools.zip.ZipOutputStream 类进行压缩，它的好处就是支持中文路径， 而Java类库中的
     * java.util.zip.ZipOutputStream 压缩中文文件名时压缩包会出现乱码。 使用 apache 中的这个类与 java
     * 类库中的用法是一新的，只是能设置编码方式了。]<BR>
     * [功能详细描述]
     *
     * @param zos
     * @param bo
     * @param srcFile
     * @param prefixDir
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void writeRecursive(ZipOutputStream zos,
                                BufferedOutputStream bo, File srcFile, String prefixDir)
            throws IOException, FileNotFoundException {
        Log.e(TAG, "writeRecursive");
        ZipEntry zipEntry;
        String filePath = srcFile.getAbsolutePath().replaceAll("////", "/")
                .replaceAll("//", "/");
        if (srcFile.isDirectory()) {
            filePath = filePath.replaceAll("/$", "") + "/";
        }
        String entryName = filePath.replace(prefixDir, "").replaceAll("/$", "");
        if (!entryName.equals(zipFileName)) {//排除.zip
            if (srcFile.isDirectory()) {
                if (!"".equals(entryName)) {
                    Log.e(TAG, "正在创建目录 - " + srcFile.getAbsolutePath()
                            + " entryName=" + entryName);
                    // 如果是目录，则需要在写目录后面加上 /
                    zipEntry = new ZipEntry(entryName + "/");
                    zos.putNextEntry(zipEntry);
                }
                File srcFiles[] = srcFile.listFiles();
                for (int i = 0; i < srcFiles.length; i++) {
                    if (!srcFiles[i].getPath().equals(srcFile.getPath() + zipFileName)) {
                        writeRecursive(zos, bo, srcFiles[i], prefixDir);
                    }
                }
            } else {
                Log.e(TAG, "正在写文件 - " + srcFile.getAbsolutePath() + " entryName="
                        + entryName);
                BufferedInputStream bi = new BufferedInputStream(
                        new FileInputStream(srcFile));
                // 开始写入新的ZIP文件条目并将流定位到条目数据的开始处
                zipEntry = new ZipEntry(entryName);
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int readCount = bi.read(buffer);
                while (readCount != -1) {
                    bo.write(buffer, 0, readCount);
                    readCount = bi.read(buffer);
                }
                // 注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
                // 然可能有的内容就会存入到后面条目中去了
                bo.flush();
                // 文件读完后关闭
                bi.close();
            }
        }

    }

}
