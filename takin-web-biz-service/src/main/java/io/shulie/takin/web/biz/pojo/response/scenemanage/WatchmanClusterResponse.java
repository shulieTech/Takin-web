package io.shulie.takin.web.biz.pojo.response.scenemanage;

import java.util.Objects;

import io.shulie.takin.adapter.api.model.response.watchman.WatchmanCluster;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchmanClusterResponse extends WatchmanCluster {

    private EngineType type;
    private boolean disable;

    public WatchmanNode getResource() {
        WatchmanNode resource = super.getResource();
        return isDisable() ? new WatchmanNode(getId()) : resource;
    }

    public boolean isDisable() {
        return Objects.isNull(super.getResource());
    }
}
