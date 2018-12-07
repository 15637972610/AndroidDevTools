package com.dkp.shopping.utils;

import android.content.Context;
import android.text.TextUtils;


import com.elvishew.xlog.XLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 号码处理工具类
public class NumberUtils {
    private static final String TAG = "NumberUtils";

    private static Context APPLICATION_CONTEXT;
    private static final String CHINAMOBILE_CSNUM = "10086"; // 中国移动10086客服
    private static final String CHINAMOBILE_HK_CSNUM = "56402332"; // 中国移动10086客服
    /**
     * 中国香港区域代号
     **/
    public static final String AREA_CODE_CHINA_HK = "+852";
    /**
     * 中国
     **/
    public static final String AREA_CODE_CHINA = "+86";

    public static final String COUNTRYCODE_DEFAULT = "+86";//默认国家码 countryCode

    /**
     * 在application中调用该方法,提供全局的context为没有context的地方提供方便
     **/
    public static void init(Context context) {
        APPLICATION_CONTEXT = context;
    }


    public static final String getformatPhone(String value) {
        if (value != null && value.length() > 2) {
            value = value.replace(" ", "");
            value = value.replace("-", "");
            StringBuffer sb = new StringBuffer(value);

            if (value.startsWith("+86") || value.startsWith("086")) {
                sb.delete(0, 3);
            } else if (value.startsWith("0086")) {
                sb.delete(0, 4);
            } else if (value.length() > 11 && value.startsWith("86")) {
                sb.delete(0, 2);
            } else if (value.startsWith("+852")) {
                sb.delete(0, 4);
            }
//			if (value.startsWith("12520") && value.length() > 5) {
//				sb.delete(0, 5);
//			}
            value = sb.toString();
        }
        if (value == null) {
            value = "";
        }
        return value;
    }

    static Pattern pattern = Pattern.compile("1[0-9]{10}");

    public static boolean isMobileNO(String mobiles) {
        Matcher matcher = pattern.matcher(mobiles);
        return matcher.matches();
    }


    //判断是否为数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将手机号格式转化 +8613260491122 132****1122
     *
     * @param person
     * @return
     */
    public static final String formatPerson(String person) {
        if (TextUtils.isEmpty(person)) {
            return "";
        }
        if (person.length() <= 3)
            return person;
        StringBuffer sb = new StringBuffer(person);
        if (person.startsWith("+86")) {
            sb.delete(0, 3).trimToSize();
        }
        return sb.toString();
    }

    public static final String formatPersonStart(String person) {
        if (TextUtils.isEmpty(person)) {
            return "";
        }
        if (person.length() <= 3)
            return person;
        StringBuffer sb = new StringBuffer(person);
        if (person.startsWith("+86")) {
            sb.delete(0, 3).trimToSize();
        }
        if (sb.length() > 8) {
            sb.replace(3, 7, "****");
        }
        return sb.toString();
    }

    public static final boolean isFormatPerson(String person) {
        if (TextUtils.isEmpty(person) || person.length() < 8) {
            return false;
        }
        String s = person.substring(3, 7);
        if (s.contains("****")) {
            return true;
        }
        return false;
    }

    /**
     * 把和多号号码前面的12583X去掉
     *
     * @param number
     * @return
     */
    public static String cropNumberByHeDuoHao(String number) {

        if (number == null || number.length() < 7) {//12583
            return number;
        }
        if (number.substring(0, 5).equals("12583")) {
            return number.substring(6, number.length());
        } else {
            return number;
        }
    }

    /**
     * 描述 ：获取可拨打的号码
     *
     * @param phone 拨打的号码
     * @return
     */
    public static String getDialablePhone(String phone) {
        if (phone.contains(";transport=")) {
            phone = phone.substring(0, phone.indexOf(";transport="));
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (isDialableChar(c)) {
                res.append(c);
            }
        }
        return res.toString();
    }

    /**
     * 描述 ：拨号中可使用的字符
     *
     * @param c 字符
     * @return 字符是否可用
     */
    private static Boolean isDialableChar(char c) {
        return c == ';' || c == ',' || c == '!' || c == '\'' || c == '='
                || c == '&' || c == ':' || c == '@' || c == '.' || c == '+'
                || c == '*' || c == '#' || (c >= '0' && c <= '9')
                || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * 给传入的手机号码添加前缀（用于输出号码）  如果本身带前缀则将号码规范为“+”开头前缀
     *
     * @param phone
     * @return
     */
    public static String getDialablePhoneWithCountryCode(String phone) {

        if(TextUtils.isEmpty(phone))
            return "";
        int length  = phone.length();
        if ( length < 4) {
            return phone;
        }

        phone = getDialablePhone(phone);
        String[] codes = new String[]{"86", "852"};
        XLog.d(TAG, "getDialablePhoneWithCountryCode before phone=" + phone);
        for (String code : codes) {
            if (phone.startsWith("+")) {
                if (phone.startsWith(code, 1)) {//判断是否包含国际码前缀
                    return phone;
                }
            } else if (phone.startsWith("00")) {
                if (phone.startsWith(code, 2)) {//判断是否包含国际码前缀
                    phone = phone.substring(2, phone.length());
                    return "+" + phone;
                }
            } else if (phone.startsWith("0")) {
                if (phone.startsWith(code, 1)) {//判断是否包含国际码前缀
                    phone = phone.substring(1, phone.length());
                    return "+" + phone;
                }
            } else if (phone.startsWith(code, 0)) {
                return "+" + phone;
            }
        }
        String areaCode = NumberUtils.getLoginNumberAreaCode(APPLICATION_CONTEXT);
        phone = areaCode + phone;
        XLog.d(TAG, "getDialablePhoneWithCountryCode end phone=" + phone);
        return phone;

    }

    /**
     * 判断是否是手机号码
     */
    public static boolean isPhoneNumber(String num) {
        if (num == null) {
            return false;
        }
//        String reg1 = "^((86)|(086)|(0086)|(\\+86)){0,1}((13[0-9])|(15[^4])|(17[0,6,7,8])|(18[0-9])|(14[5,7]))\\d{8}$";
        String reg1 = "^((86)|(086)|(0086)|(\\+86)){0,1}\\d{11}$";//        String reg2 = "^((852)|(00852)|(\\+852)){0,1}\\d{8}$";
//        String reg2 = "^((852)|(00852)|(\\+852)){0,1}\\d{8}$";
        String reg2 = "^((852)|(00852)|(\\+852)){1}\\d{8}$";

        if (num.startsWith("-")) {
            num = "-" + numberFilter(num);
        } else
            num = numberFilter(num);

        return !TextUtils.isEmpty(num) ? num.matches(reg1) || num.matches(reg2) : false;
    }

    public static String numberFilter(String number) {
        if (!TextUtils.isEmpty(number)) {
            return number.replace("-", "").replace(".", "").replace(" ", "");
        }
        return number;
    }


    /**
     * 获取当前号码的区域代号
     *
     * @param context
     * @return
     */
    public static String getLoginNumberAreaCode(Context context) {
//        String phone = LoginDaoImpl.getInstance().queryLoginUser(context);
        String phone = "";
        if (phone.startsWith(AREA_CODE_CHINA)) {
            return AREA_CODE_CHINA;
        } else if (phone.startsWith(AREA_CODE_CHINA_HK)) {
            return AREA_CODE_CHINA_HK;
        }
        return AREA_CODE_CHINA;
    }

    /**
     * 判断登录号码是否是香港号
     *
     * @param context
     * @return
     */
    public static Boolean isHKLoginNum(Context context) {
        if (null != context && AREA_CODE_CHINA_HK.equals(getLoginNumberAreaCode(context))) {
            return true;
        }
        return false;
    }

    /**
     * 将10086客服号码替换为香港客服号码
     *
     * @param context
     * @param strMsg
     * @return
     */
    public static String replaceHKCS(Context context, String strMsg) {
        if (!TextUtils.isEmpty(strMsg)) {
            strMsg = strMsg.replace(CHINAMOBILE_CSNUM, CHINAMOBILE_HK_CSNUM);
        }
        return strMsg;
    }
}
