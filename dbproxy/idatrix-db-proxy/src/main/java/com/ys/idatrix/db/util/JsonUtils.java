package com.ys.idatrix.db.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

/**
 * @ClassName: JsonUtils
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/9/19
 */
public class JsonUtils {



    /**
     * 对象输出为Json格式
     *
     * @param obj
     * @param noConvertDefaultValue 不能转换（转换异常）给定的默认值
     * @return
     */
    public static String objectToJsonString(Object obj,String noConvertDefaultValue){
        String jsonString;
            try {
                jsonString = JSONObject.toJSONString(obj, true);
            } catch (Exception e) {
                try {
                    jsonString = new Gson().toJson(obj);
                } catch (Exception e1) {
                    jsonString = noConvertDefaultValue;
                }
            }
        return jsonString;
    }

}
