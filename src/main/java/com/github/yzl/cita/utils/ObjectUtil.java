package com.github.yzl.cita.utils;

public class ObjectUtil {

    private ObjectUtil() {}

    public static void checkNull(Object obj) {
        if (null == obj) {
            throw new NullPointerException(obj + " is null");
        }
    }
}
