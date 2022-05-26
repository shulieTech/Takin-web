package io.shulie.takin.adapter.api.model.request.cloud.resources;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

@Data
public class CloudResourcesRequest extends ContextExt {
    private int jobId;
    private String resourceId;
}
