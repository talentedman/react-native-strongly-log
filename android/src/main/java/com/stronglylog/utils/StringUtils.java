package com.stronglylog.utils;

import android.text.TextUtils;
public class StringUtils {
    /**
     * 对给定字符进行 URL 解码
     *
     * @param value 解码前的字符串
     * @return 解码后的字符串
     */
    public static String decode(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (Exception ex) {
            return value;
        }
    }
}
