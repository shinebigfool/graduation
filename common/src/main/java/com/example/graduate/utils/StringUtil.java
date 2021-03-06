package com.example.graduate.utils;

import java.util.Random;

public class StringUtil {
    /**
     * 字符串转为int
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
     * object转int
     *
     * @param object 异常返回-1
     * @return int
     */
    public static int objectToInt(Object object) {
        return ch2Int(parseString(object));
    }
    /**
     * 处理字符串""/toString().trim()
     *  object转string
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
    /**
     * 指定String串长度，生成随机String串
     *
     * @param length 长度
     * @return java.lang.String
     */
    public static String getRandomString(int length){
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i<length;i++){
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
