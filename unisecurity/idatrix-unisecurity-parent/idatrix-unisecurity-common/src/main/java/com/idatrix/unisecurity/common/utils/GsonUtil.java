package com.idatrix.unisecurity.common.utils;

import com.google.gson.Gson;

/**
 * @author oyr
 * @Description: ${todo}
 * @date 2018/5/28 0028上午 8:56
 */
public class GsonUtil {

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public static String toJson(Object object){
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        return gson.fromJson(json, classOfT);
    }

/*



    public static <T> T fromJson(String json, Class<T> classOfT){
        return gson.fromJson(json, classOfT);
    }
    return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
    }.getType());
*/

}
