package io.shulie.takin.adapter.api.model.request.resource;

import java.math.BigDecimal;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest.WATCH_MAN_ID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceLockRequest extends ContextExt {

    private BigDecimal cpu;
    private BigDecimal memory;
    private Integer number;
    private String image;
    private Long watchmanId = WATCH_MAN_ID;
    private String callbackUrl;
}
