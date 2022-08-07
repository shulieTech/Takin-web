package io.shulie.takin.adapter.api.model.request.cloud.resources;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CloudResourcesRequest extends ContextExt {
    private int pressureId;
    private String resourceId;
}
