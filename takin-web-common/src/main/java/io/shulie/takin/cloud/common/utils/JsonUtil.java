package io.shulie.takin.cloud.common.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyuanba
 * @date 2021/10/18 4:41 下午
 */
@Slf4j
public class JsonUtil {
    public static String toJson(Object o) {
        if (null == o) {
            return null;
        }
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            log.error("toJson failed!o=" + o);
        }
        return null;
    }

    public static JSONObject parse(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        JSONObject json = null;
        try {
            json = JSON.parseObject(text);
        } catch (Exception e) {
            log.error("parse json failed!text=" + text);
        }
        return json;
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        T result = null;
        try {
            result = JSON.parseObject(text, clazz);
        } catch (Exception e) {
            log.error("parse json to object class failed!text=" + text);
        }
        return result;
    }

    public static <T> T parseObject(String text, TypeReference<T> type) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        T result = null;
        try {
            result = JSON.parseObject(text, type);
        } catch (Exception e) {
            log.error("parse json to object type failed!text=" + text);
        }
        return result;
    }

    public static JSONArray parseArray(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        JSONArray result = null;
        try {
            result = JSON.parseArray(text);
        } catch (Exception e) {
            log.error("json parseArray failed!text=" + text);
        }
        return result;
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        List<T> result = null;
        try {
            result = JSON.parseArray(text, clazz);
        } catch (Exception e) {
            log.error("json parseArray failed!text=" + text);
        }
        return result;
    }

}
