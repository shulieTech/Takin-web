package io.shulie.takin.adapter.api.model.request.watchman;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WatchmanRegisterRequest extends ContextExt {

    private String publicKey;
    private String attach;
}
