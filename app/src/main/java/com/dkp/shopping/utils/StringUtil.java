package com.dkp.shopping.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


import java.text.Normalizer;
import java.util.Random;

/**
 * Created by DKP on 2018/7/12.
 */

public class StringUtil {
    public static final String EMAIL_MARK = "@";
    public static String removeWhiteSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }

    public static String replaceWhiteSpaces(String str) {
        return str.replaceAll("\\s+", "_");
    }

    public static String normalizeString(String str) {
        if (str == null) {
            return null;
        }
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static boolean isEmpty(CharSequence charSequence) {
        if (charSequence == null || TextUtils.isEmpty(charSequence.toString().trim())) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    public static int getRandom(int i, int i2) {
        if (i < i2) {
            return new Random().nextInt(Math.abs(i2 - i)) + i;
        }
        return i - new Random().nextInt(Math.abs(i2 - i));
    }

    public static boolean isEquals(String str, String str2) {
        if (isEmpty(str) && isEmpty(str2)) {
            return true;
        }
        if (isEmpty(str) || isEmpty(str2)) {
            return false;
        }
        if (str == str2 || str.equals(str2)) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String $(String str) {
        int intValue = Integer.valueOf(str).intValue();
        if (intValue < 10) {
            return "0" + String.valueOf(intValue);
        }
        return str;
    }

    public static boolean isNum(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static boolean isValidEmail(String mail) { //邮箱规则定义：检测到有且只有一个@，@前后有内容，其他无效
        int emailMark = 0;
        if (mail.contains(EMAIL_MARK) && !mail.startsWith(EMAIL_MARK) && !mail.endsWith(EMAIL_MARK)) {
            for (int i = 0; i < mail.length(); i++) {
                if (EMAIL_MARK.equals(String.valueOf(mail.charAt(i)))) {
                    if (emailMark > 1) {
                        return false;
                    }
                    emailMark++;
                }
            }
            if (emailMark == 1) {
                return true;
            }
        }
        return false;
    }


    /**
     * 复制字符串到剪贴板
     */
    public static void CopyStringToClipboard(final Context mContext, final String str) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clip != null) {
                clip.setText(str);// 设置Clipboard 的内容
            }
        } else {
            ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            if (manager != null) {
                manager.setText(str);
            }
        }
    }

    /**
     * 首字母转大写
     */
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
