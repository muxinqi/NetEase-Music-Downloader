package com.mtools.android.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Utility {
    /**
     * 使用Jackson将json反序列化为实体对象
     *     jsonResponse - 传入的json字符串
     *     Class<T> - 返回类的类型
     *     failOnUnknownProperties - 转换时是否不忽略类中未知的属性
     * */
    public static <T> T handleJsonToObject(String jsonResponse, Class<T> theClass, boolean failOnUnknownProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        T result = null;
        try {
            result = objectMapper.readValue(jsonResponse, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
