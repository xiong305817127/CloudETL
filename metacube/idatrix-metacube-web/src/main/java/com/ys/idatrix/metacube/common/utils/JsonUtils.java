package com.ys.idatrix.metacube.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @ClassName JsonUtils
 * @Description json工具类
 * @Author ouyang
 * @Date
 */
public class JsonUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    // javaBean to json
    public static String toJson(Object object){
        if(object==null){
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // json to javaBean
    public static <T> T toJavaBean(String json, Class<T> classType){
        if(json==null){
            return null;
        }
        try {
            T t = objectMapper.readValue(json, classType);
            return t;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
