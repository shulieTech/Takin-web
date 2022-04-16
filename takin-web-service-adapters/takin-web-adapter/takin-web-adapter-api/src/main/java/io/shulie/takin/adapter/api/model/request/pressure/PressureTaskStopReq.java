package io.shulie.takin.adapter.api.model.request.pressure;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PressureTaskStopReq extends ContextExt {

    private Long jobId;
}
