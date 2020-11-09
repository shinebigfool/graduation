package com.example.graduate.utils;

public class StringUtil {
    /**
     * 将字符串转换为int型整数。
     *
     * @param str 目标字符串
     * @return int
     */
    public static int ch2Int(String str) {
        try {
            return (Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return (-1);
        }
    }
    /**
     *
     *
     * @param object
     * @return int
     */
    public static int objectToInt(Object object) {
        return ch2Int(parseString(object));
    }
    /**
     * 处理字符串
     * 
     * @param arg0
     * @return 若obj为空(null)则返回"",否则返回obj转换成字符串且除去该字符前后空格之后的值
     */
    public static String parseString(Object arg0) {
        return arg0 == null || "null".equalsIgnoreCase(arg0.toString()) ? "" : arg0.toString().trim();
    }

    /**
     * 字符串是否为空
     * 与Empty的区别是对于\t \r \n 等特殊字符都属于true
     *
     * @param cs 字符串
     * @return boolean
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
