package io.shulie.takin.adapter.api.model.request.excess;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataCalibrationRequest extends ContextExt {

    private Long jobId;
    private String resourceId;
}
