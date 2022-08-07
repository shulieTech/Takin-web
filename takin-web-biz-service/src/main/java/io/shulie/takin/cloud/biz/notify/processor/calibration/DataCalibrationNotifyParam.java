package io.shulie.takin.cloud.biz.notify.processor.calibration;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.Calibration;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("302")
public class DataCalibrationNotifyParam extends CloudNotifyParam {

    private Calibration.Data data;
    private String source; // 来源
}
