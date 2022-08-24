package io.shulie.takin.adapter.api.model.request.watchman;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchmanBaseRequest extends ContextExt {

    private String watchmanId;
}
