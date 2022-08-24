package io.shulie.takin.adapter.api.model.request.pressure;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PressureParamsReq extends ContextExt {

    private Long pressureId;
    /**
     * 修改的关联节点
     */
    private String ref;
}
