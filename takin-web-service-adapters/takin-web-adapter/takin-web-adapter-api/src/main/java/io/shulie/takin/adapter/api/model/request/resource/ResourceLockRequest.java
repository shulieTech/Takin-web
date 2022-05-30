package io.shulie.takin.adapter.api.model.request.resource;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest.WATCH_MAN_ID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceLockRequest extends ContextExt {

    private String cpu;
    private String memory;
    private String limitCpu;
    private String limitMemory;
    private Integer number;
    private String image;
    private Long watchmanId = WATCH_MAN_ID;
    private String callbackUrl;
}
