package io.shulie.takin.adapter.api.model.request.watchman;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchmanListRequest extends WatchmanBatchRequest {

    private Integer pageNumber = 1;
    private Integer pageSize = Integer.MAX_VALUE;
}
