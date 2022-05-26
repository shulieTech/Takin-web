package io.shulie.takin.cloud.biz.notify.processor.calibration;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.constant.enums.ExcessJobType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("302")
public class DataCalibrationNotifyParam extends CloudNotifyParam {

    private Long data;
    private ExcessJobType jobType;
    private Long jobId;
    private String content;
    private Boolean completed;
    private String resourceId;
    private String source; // 来源
}
