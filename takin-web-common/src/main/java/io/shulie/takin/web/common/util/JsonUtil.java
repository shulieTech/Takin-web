package io.shulie.takin.web.common.util;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import org.apache.commons.lang3.StringUtils;

/**
 * json 工具类
 * 使用 alibaba JSONObject
 *
 * @author loseself
 * @date 2021/3/27 7:11 下午
 **/
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * object 转 json
     *
     * @param object 实例
     * @return json
     */
    public static String bean2Json(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * json 转 复杂实例
     *
     * @param json json
     * @param type 实例对应的类泛型
     * @return 实例
     */
    @Deprecated
    public static <T> T json2bean(String json, TypeReference<T> type) {
        return JSONObject.parseObject(json, type);
    }

    /**
     * json 转 实例
     *
     * @param json json
     * @param clazz 实例对应的类
     * @param <T> 实例对应的类泛型
     * @return 实例
     */
    @Deprecated
    public static <T> T json2bean(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }


    /**
     * json 转 复杂实例
     *
     * @param json json
     * @param type 实例对应的类泛型
     * @return 实例
     */
    public static <T> T json2Bean(String json, TypeReference<T> type) {
        return JSONObject.parseObject(json, type);
    }

    /**
     * json 转 实例
     *
     * @param json json
     * @param clazz 实例对应的类
     * @param <T> 实例对应的类泛型
     * @return 实例
     */
    public static <T> T json2Bean(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

    /**
     * json 转 list
     *
     * @param json json
     * @param clazz 实例对应的类
     * @param <T> 实例对应的类泛型
     * @return 实例
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        return JSONObject.parseArray(json, clazz);
    }

    /**
     * 判断字符串是否json格式
     *
     * @param content
     */
    public static boolean isJson(String content) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        boolean isJsonObject = true;
        boolean isJsonArray = true;
        try {
            JSONObject.parseObject(content);
        } catch (Exception e) {
            isJsonObject = false;
        }
        try {
            JSONObject.parseArray(content);
        } catch (Exception e) {
            isJsonArray = false;
        }
        if (!isJsonObject && !isJsonArray) {
            return false;
        }
        return true;
    }
}
