package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.basic.JobExample;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("202")
public class PressureStopNotifyParam extends CloudNotifyParam {
    private JobExample data;
}
