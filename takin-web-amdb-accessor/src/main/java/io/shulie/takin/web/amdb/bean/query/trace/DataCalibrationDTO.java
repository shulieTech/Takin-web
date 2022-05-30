package io.shulie.takin.web.amdb.bean.query.trace;

import lombok.Data;

@Data
public class DataCalibrationDTO {

    private Long jobId;
    private String resourceId;
    private String callbackUrl;
}
