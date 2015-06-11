package lx.af.utils;

import android.app.Application;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by liuxu. many methods here are collected from various sites.
 *
 * operation about string.
 */
public final class StringUtils {

    private static Application sApp;

    private StringUtils() {
    }

    public static void init(Application app) {
        sApp = app;
    }


    // ==========================================================
    // about md5

    /**
     * get md5 string according to source data
     * @param data the source data
     * @return the md5 string
     */
    public static String toMd5(byte[] data) {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(data);
            result = toHexString(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * get md5 string according to source
     * @param str the source string
     * @return the md5 string
     */
    public static String toMd5(String str) {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(str.getBytes("utf-8"));
            result = toHexString(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得文件的MD5值
     * @param file
     * @return
     */
    public static String toMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * get md5 string according to source. the returned md5 string has 16 bit.
     * @param str the source string
     * @return the md5 string
     */
    public static String to16bitMd5(String str) {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(str.getBytes("utf-8"));
            result = toHexString(algorithm.digest()).substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int b : bytes) {
            if (b < 0) {
                b += 256;
            }
            if (b < 16) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(b));
        }
        return hexString.toString();
    }

    /**
     * BASE64 encrypt
     * @param str the origin string
     * @return the encrypted string
     */
    public static  String encryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            return new String(Base64.encode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * BASE64 decrypt
     * @param str the encrypted string
     * @return the decrypted string
     */
    public static  String decryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    // ==========================================================
    // verify methods

    /**
     * see if the string is empty: null, or zero length, or only contains \t, \r, \n
     * @param str string
     * @return true if string is empty
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || "".equals(str))
            return true;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * check if the given string is a valid mobile number.
     * @param mobile source string
     * @return true if valid
     */
    public static boolean isMobile(CharSequence mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        Pattern patternMobile = Pattern
                .compile("^[1][3,4,5,7,8][0-9]{9}$");
                //.compile("^((13[0-9])|(15[^4,\\D])|(18[0,4-9]))\\d{8}$");
        Matcher m = patternMobile.matcher(mobile);
        return m.matches();
    }

    /**
     * check if the given string is a valid email address.
     * @param email source string
     * @return true if valid
     */
    public static boolean isEmail(CharSequence email) {
        if (isEmpty(email)) {
            return false;
        }
        Pattern patternEmail = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = patternEmail.matcher(email);
        return m.matches();
    }

    /**
     * check if the string contains only number
     * @return boolean
     */
    public static boolean isDigit(String number) {
        Pattern patternDigit = Pattern.compile("[0-9]{1,}");
        Matcher matcher = patternDigit.matcher(number);
        return matcher.matches();
    }

    /**
     * check if password is valid.
     * only digit, alphabet and '_' is allowed.
     * @param password the password
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]{6,20}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * check two string is equals or not.
     * @return boolean
     */
    public static boolean isEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 != null && str2 != null) {
            return str1.equals(str2);
        }
        return false;
    }

    /**
     * check two string is equals or not, ignore case.
     * @return boolean
     */
    public static boolean isEqualIgnoreCase(String str1, String str2) {
        return isEqual(
                str1 == null ? null : str1.toLowerCase(),
                str2 == null ? null : str2.toLowerCase());
    }

    /**
     * compare two strings, mainly used for Comparator.
     * see Comparator.compare() for detail
     * @return see Comparator.compare() for detail
     */
    public static int compare(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return 0;
        }
        if (str1 != null && str2 == null) {
            return 1;
        }
        if (str1 == null && str2 != null) {
            return -1;
        }
        return str1.compareTo(str2);
    }

    // ==========================================================
    // about url and uri


    /**
     * Check the url is valid or not.
     * @param url
     *            the url need check
     * @return boolean
     */
    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 找到一个字符串里面的所有img标签中的url。
     * @return
     */
    public static List<String> findImageUrls(String rawData) {
        String patternImgStr = "<img[\\s]*[^>]+>";
        Pattern imgLabelPattern = Pattern.compile(patternImgStr);
        Matcher matcher = imgLabelPattern.matcher(rawData);
        List<String> imgUrls = new LinkedList<String>();
        while (matcher.find()) {
            imgUrls.add(matcher.group(0));
        }
        ArrayList<String> resultUrls = new ArrayList<String>();
        for (Iterator<String> iterator = imgUrls.iterator(); iterator.hasNext();) {
            resultUrls.add(iterator.next().replaceAll("<img.*?src=\"", "")
                    .replaceAll("\".*", ""));
        }
        return resultUrls;
    }

    /**
     * add query string for url
     * @param url url
     * @param queryString query string
     * @return url with query string
     */
    public static String addQueryString(String url, String queryString) {
        try {
            URI uri = URI.create(url);
            if (uri.getQuery() == null) {
                return url + "?" + queryString;
            } else {
                return url + "&" + queryString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    // ==========================================================


    /**
     * 把十六进制的String转为int。 如 "3f3f3f" -> 4144959
     * @return
     * @throws Exception
     */
    public static int hexStr2Integer(String hexStr) {
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();

        map.put('0', 0);
        map.put('1', 1);
        map.put('2', 2);
        map.put('3', 3);
        map.put('4', 4);
        map.put('5', 5);
        map.put('6', 6);
        map.put('7', 7);
        map.put('8', 8);
        map.put('9', 9);
        map.put('a', 10);
        map.put('b', 11);
        map.put('c', 12);
        map.put('d', 13);
        map.put('e', 14);
        map.put('f', 15);
        map.put('A', 10);
        map.put('B', 11);
        map.put('C', 12);
        map.put('D', 13);
        map.put('E', 14);
        map.put('F', 15);

        int result = 0;
        String tmpString = hexStr;
        if ('#' == tmpString.charAt(0)) {
            tmpString = hexStr.substring(1, tmpString.length());
        }
        for (int i = 0; i < tmpString.length(); i++) {
            result <<= 4;
            if (null == map.get(tmpString.charAt(i))) {
                throw new InvalidParameterException(
                        "hexStr2Integer invalid parameter");
            }
            result += map.get(tmpString.charAt(i));
        }
        return result;
    }

    /**
     * 逆序每隔3位添加一个逗号
     * @param str
     *      :"31232"
     * @return
     *      :"31,232"
     */
    public static String addComma3(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        str = new StringBuilder(str).reverse().toString();
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        return new StringBuilder(str2).reverse().toString();
    }

    /**
     * return a String represents the current time.
     * @param format format of the time string, like "yyyyMMddHHmmss"
     * @return a String represents the current date and time.
     */
    public static String getCurrentTimeString(String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(new Date());
    }

    /**
     * Join all the elements of a string array into a single String. If the given array
     * empty an empty string will be returned. Null elements of the array are  allowed and
     * will be treated like empty Strings.
     * for example, join(new String[] {"127", "0", "0", "1"}, ".") will get 127.0.0.1
     * @param array Array to be joined into a string.
     * @param delimiter String to place between array elements.
     * @return Concatenation of all the elements of the given array with the the delimiter
     *         in between.
     * @throws NullPointerException if array or delimiter is null.
     */
    public static String join(String[] array, String delimiter) {
        // Cache the length of the delimiter has the side effect of throwing
        // a NullPointerException if the delimiter is null.
        int delimiterLength = delimiter.length();
        // Nothing in the array return empty string has the side effect of
        // throwing a NullPointerException if the array is null.
        if (array.length == 0)
            return "";
        // Only one thing in the array, return it.
        if (array.length == 1) {
            if (array[0] == null)
                return "";
            return array[0];
        }
        // Make a pass through and determine the size of the resulting string.
        int length = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                length += array[i].length();
            if (i < array.length - 1)
                length += delimiterLength;
        }
        // Make a second pass through and concatenate everything into a string buffer.
        StringBuffer result = new StringBuffer(length);
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                result.append(array[i]);
            if (i < array.length - 1)
                result.append(delimiter);
        }
        return result.toString();
    }


    // ==========================================================
    // about resource string


    /**
     * Return a localized string from the application's package's
     * default string table.
     * @param resId Resource id for the string
     */
    public static String getResString(int resId) {
        return sApp.getString(resId);
    }

    /**
     * Return a localized string from the application's package's
     * default string table.
     * @param resId Resource id for the string
     * @param formatArgs The format arguments that will be used for substitution.
     */
    public static String getResString(int resId, Object... formatArgs) {
        return sApp.getString(resId, formatArgs);
    }

}
