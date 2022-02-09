package io.shulie.takin.web.api.request;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class TakinWebSceneRequest extends TakinWebBaseRequest implements Serializable {
    private Long sceneId;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }
}
