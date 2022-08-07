package io.shulie.takin.adapter.api.model.request.watchman;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WatchmanRegisterUpdateRequest extends WatchmanBaseRequest {

    private String publicKey;
}
