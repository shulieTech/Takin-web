package io.shulie.takin.adapter.api.model.request.resource;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceUnLockRequest extends ContextExt {

    private String resourceId;
}
