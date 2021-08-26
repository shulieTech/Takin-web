package com.pamirs.takin.common.util;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * json 工具类
 * 使用 alibaba JSONObject
 * 重复了, 不使用
 *
 * 使用下面
 * @see io.shulie.takin.web.common.util.JsonUtil
 *
 * @author loseself
 * @date 2021/3/27 7:11 下午
 **/
@Deprecated
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
     * json 转 实例
     *
     * @param json json
     * @param clazz 实例对应的类
     * @param <T> 实例对应的类泛型
     * @return 实例
     */
    public static <T> T json2bean(String json, Class<T> clazz) {
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

}
