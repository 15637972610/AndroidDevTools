package com.dkp.shopping.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA加密
 */

public class SHA {
    public static String getSha1(String str){
        if(str == null || str.length() == 0){
            return null;
        }
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for(int i =0;i<j;i++){
                byte byteO = md[i];
                buf[k++] = hexDigits[byteO >>> 4 & 0xf];
                buf[k++] = hexDigits[byteO & 0xf];
            }
            String s = new String(buf);
            return s.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
