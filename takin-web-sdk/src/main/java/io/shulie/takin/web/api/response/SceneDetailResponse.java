package io.shulie.takin.web.api.response;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.web.api.model.SceneGoalModel;

/**
 * @author caijianying
 */
public class SceneDetailResponse implements Serializable {
    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 业务流程ID
     */
    private Long businessFlowId;

    /**
     * 脚本ID
     */
    private Long scriptId;

    /**
     * 目标
     */
    private List<SceneGoalModel> goals;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public Long getBusinessFlowId() {
        return businessFlowId;
    }

    public void setBusinessFlowId(Long businessFlowId) {
        this.businessFlowId = businessFlowId;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public List<SceneGoalModel> getGoals() {
        return goals;
    }

    public void setGoals(List<SceneGoalModel> goals) {
        this.goals = goals;
    }
}
