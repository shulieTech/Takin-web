package io.shulie.takin.adapter.api.model.response.watchman;

import lombok.Data;

@Data
public class WatchmanCluster {

    private String id;
    private String attach;
    private String sign;
    private WatchmanNode resource;
}
