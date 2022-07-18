package com.example.wangyiyun.utils;

import java.util.ArrayList;
import java.util.List;

public class ListChangeUtil {
    /**
     * 类型转换
     *
     * @param obj   待转换对象
     * @param clazz list类型
     * @param <T>   泛型
     * @return
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
