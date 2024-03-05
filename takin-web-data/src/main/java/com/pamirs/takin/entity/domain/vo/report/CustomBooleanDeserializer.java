package com.pamirs.takin.entity.domain.vo.report;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author zhangz
 * Created on 2024/3/5 19:59
 * Email: zz052831@163.com
 */

public class CustomBooleanDeserializer  extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        return "1".equals(value) || Boolean.parseBoolean(value);
    }
}
