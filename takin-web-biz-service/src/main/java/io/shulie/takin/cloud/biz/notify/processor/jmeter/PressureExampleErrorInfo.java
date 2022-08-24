package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PressureExampleErrorInfo extends PressureExample {

    private String errorMessage;
}
