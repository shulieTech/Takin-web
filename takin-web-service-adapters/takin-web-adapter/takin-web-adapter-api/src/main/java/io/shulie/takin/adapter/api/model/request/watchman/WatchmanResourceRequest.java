package io.shulie.takin.adapter.api.model.request.watchman;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest.WATCH_MAN_ID;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchmanResourceRequest extends ContextExt {

    private Long watchmanId = WATCH_MAN_ID;
}
