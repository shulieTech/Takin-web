package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("203")
public class PressureErrorNotifyParam extends CloudNotifyParam {

    private JobExampleErrorInfo data;
}
