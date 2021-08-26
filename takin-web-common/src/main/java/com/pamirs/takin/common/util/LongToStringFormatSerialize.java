package com.pamirs.takin.common.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * long型转换成字符串序列化类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月21日
 */
public class LongToStringFormatSerialize extends JsonSerializer<Long> {

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s 字符串
     * @return -
     * @author shulie
     * @date 2018年5月21日
     */
    public String subZeroAndDot(String s) {
        if (StringUtils.contains(s, ".")) {
            //去掉多余的0
            s = s.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    /**
     * 继承父类的serialize方法
     *
     * @param value       字符串value
     * @param gen         JsonGenerator gen
     * @param serializers JsonGenerator serializers
     * @author shulie
     * @date 2018年5月21日
     */
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeString(subZeroAndDot(value.toString()));
    }

}
