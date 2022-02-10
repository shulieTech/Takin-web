package io.shulie.takin.web.api.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author caijianying
 */
public class SceneListResponse implements Serializable {
    /**
     * 场景ID
     */
    private Long id;
    /**
     * 场景名称
     */
    private String sceneName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
}
