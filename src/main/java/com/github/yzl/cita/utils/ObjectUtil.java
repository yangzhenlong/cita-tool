package com.github.yzl.cita.utils;

public class ObjectUtil {

    private ObjectUtil() {}

    public static void checkNull(Object obj) {
        if (null == obj) {
            throw new NullPointerException(obj + " is null");
        }
    }

    public static boolean isNull(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            return StringUtil.isBlank(str);
        }
        return null == obj;
    }
}
