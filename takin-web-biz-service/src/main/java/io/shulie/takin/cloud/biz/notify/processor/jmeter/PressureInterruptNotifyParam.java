package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("204")
public class PressureInterruptNotifyParam extends CloudNotifyParam {

    private PressureExample data;
}
