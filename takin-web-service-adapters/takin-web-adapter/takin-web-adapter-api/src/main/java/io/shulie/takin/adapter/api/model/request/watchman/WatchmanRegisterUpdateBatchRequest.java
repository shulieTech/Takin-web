package io.shulie.takin.adapter.api.model.request.watchman;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WatchmanRegisterUpdateBatchRequest extends WatchmanBatchRequest {

    private String publicKey;
}
