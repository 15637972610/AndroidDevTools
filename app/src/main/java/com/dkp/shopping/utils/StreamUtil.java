package com.dkp.shopping.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class StreamUtil {

	/**
	 * 根据文件路径new一个文件输入流
	 */
	public static synchronized InputStream loadStreamFromFile(
			String filePathName) throws FileNotFoundException, IOException {
		return new FileInputStream(filePathName);
	}

	/**
	 * 将String保存到指定的文件中
	 */
	public static void saveStringToFile(String text, String filePath) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
		saveStreamToFile(in, filePath);
	}

	/**
	 * 将InputStream保存到指定的文件中
	 */
	public static synchronized void saveStreamToFile(InputStream in, String filePath) throws IOException {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			} else {
			    File parent = file.getParentFile();
			    if (!parent.exists()) {
                    parent.mkdirs();
                }
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			copyStream(in, fos);
			fos.close();
		} catch (Exception e) {
//		    if (DLog.isLog) {
//                DLog.e("StreamUtil", e.getMessage(), e);
//            }
		}
	}

	/**
	 * 从输入流里面读出byte[]数组
	 */
	public static byte[] readStream(InputStream in) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = in.read(buf)) != -1) {
			byteOut.write(buf, 0, len);
		}
		
		byteOut.close();
		in.close();
		return byteOut.toByteArray();
	}

	/**
	 * 从输入流里面记载String
	 */
//	public static String loadStringFromStream(InputStream in) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream(16384);
//		copyStream(in, baos);
//		baos.close();
//		return baos.toString("UTF-8");
//	}

	/**
	 * 从输入流里面读出每行文字
	 */
	public static ArrayList<String> loadStringLinesFromStream(InputStream in) throws IOException {
		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		String row;
		ArrayList<String> lines = new ArrayList<String>();
		while ((row = br.readLine()) != null) {
			lines.add(row);
		}
		br.close();
		reader.close();
		return lines;
	}

	/**
	 * 拷贝流
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);

		byte[] buffer = new byte[4096];

		while (true) {
			int doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bout.flush();
	}

	/**
	 * 刷新输入流
	 */
	public static ByteArrayInputStream flushInputStream(InputStream in) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bout = new BufferedOutputStream(baos);

		byte[] buffer = new byte[4096];
		while (true) {
			int doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bout.flush();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return bais;
	}

	/**
	 * 将输入流转化为字符串输出
	 */
	public static final String getStringFromInputStream(InputStream is) {
		if (is != null) {
			StringBuffer buf = new StringBuffer();
				ArrayList<String> als;
				try {
					als = loadStringLinesFromStream(is);
					for (String string : als)
						buf.append(string);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return buf.toString();
		}
		return "";
	}
}
