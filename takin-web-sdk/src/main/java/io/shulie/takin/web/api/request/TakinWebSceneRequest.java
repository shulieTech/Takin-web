package io.shulie.takin.web.api.request;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class TakinWebSceneRequest extends TakinWebBaseRequest implements Serializable {
    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }
}
