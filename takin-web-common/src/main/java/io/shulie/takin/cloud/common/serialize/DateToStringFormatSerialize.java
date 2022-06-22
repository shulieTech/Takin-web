package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 日期转换成字符串工具类(序列化JSON用)
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月16日
 */
public class DateToStringFormatSerialize extends JsonSerializer<Date> {
    /**
     * 继承父类的serialize方法
     *
     * @param date 日期
     * @param gen  JsonGenerator gen
     * @author shulie
     * @date 2018年5月21日
     */
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (date == null) {
            gen.writeString("");
        } else {
            gen.writeString(DateUtil.formatDateTime(date));
        }
    }
}
