package com.pamirs.takin.common.util;

import java.util.Date;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 说明:
 *
 * @author shulie
 * @version v1.0
 * @date 2018/8/23 17:36
 */
public class DateToLongFormatSerialize extends JsonSerializer<Date> {

    /**
     * 继承父类的serialize方法
     *
     * @param date 日期
     * @param gen  JsonGenerator gen
     * @author shulie
     * @date 2018年5月21日
     */
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        if (date == null) {
            gen.writeString("");
        } else {
            gen.writeString(String.valueOf(date.getTime()));
        }
    }
}
