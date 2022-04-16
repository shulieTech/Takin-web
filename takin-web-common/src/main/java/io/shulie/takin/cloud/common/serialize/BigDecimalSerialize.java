package io.shulie.takin.cloud.common.serialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author 何仲奇
 * @date 2020/10/15 4:56 下午
 */
public class BigDecimalSerialize extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        if (value != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            gen.writeNumber(df.format(value));
        } else {
            gen.writeNumber(0.00);
        }
    }
}
