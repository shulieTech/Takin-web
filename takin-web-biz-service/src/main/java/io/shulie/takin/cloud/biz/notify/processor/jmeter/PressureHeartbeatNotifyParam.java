package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("200")
public class PressureHeartbeatNotifyParam extends CloudNotifyParam {

    private PressureExample data;
}
