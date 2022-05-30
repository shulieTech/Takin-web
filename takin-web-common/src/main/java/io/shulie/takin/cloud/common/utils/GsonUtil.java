package io.shulie.takin.cloud.common.utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 说明: 序列化
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/7/16 10:18
 */
@Slf4j
public class GsonUtil {

    /**
     * 空的 {@code String} 数据 - <code>""""</code>。
     */
    public static final String EMPTY_STRING = "";
    /**
     * 空的 {@code JSON} 数据 - <code>"{}"</code>。
     */
    public static final String EMPTY_JSON = "{}";
    /**
     * 默认的 {@code JSON} 日期/时间字段的格式化模式。
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final Gson GSON = new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        //            .generateNonExecutableJson()
        //            .registerTypeAdapter()
        //            .registerTypeAdapterFactory(new CglibProxyFactory())
        .registerTypeAdapterFactory(new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                return null;
            }
        })
        .setDateFormat(DEFAULT_DATE_PATTERN)
        .setPrettyPrinting()
        .create();

    public GsonUtil() {
    }

    /**
     * 说明: 将对象序列化为字符串
     *
     * @param object 任意对象
     * @return json字符串
     * @author shulie
     * @date 2018/7/16 10:22
     */
    public static String gsonToString(Object object) {
        String result = "";
        try {
            if (object != null) {
                result = GSON.toJson(object);
            }
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder("object = {");
            if (object instanceof Map) {
                Map<?, ?> mapObj = (Map<?, ?>)object;
                for (Object key : mapObj.keySet()) {
                    Object value = mapObj.get(key);
                    builder.append(key).append("=").append(value.toString()).append(",");
                }
            }
            builder.append("}");
            log.error("异常代码【{}】,异常内容：gsonToString失败,内容为:{} --> 异常信息: {}",
                TakinCloudExceptionEnum.JSON_PARSE_ERROR, builder, e);

        }
        return EMPTY_JSON.equals(result) ? GsonUtil.EMPTY_STRING : result;
    }

    /**
     * 说明: json字符串反序列化为bean
     *
     * @param json   json字符串
     * @param tClass 实体类class对象
     * @return 反序列化对象
     * @author shulie
     * @date 2018/7/16 10:22
     */
    public static <T> T gsonToBean(String json, Class<T> tClass) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return GSON.fromJson(json, tClass);
    }

}
