package com.github.yzl.cita.utils;

public class StringUtil {

    private StringUtil() {}

    public static boolean isBlank(String str) {
        return null == str || str.length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
