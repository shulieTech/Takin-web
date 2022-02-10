package io.shulie.takin.web.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author caijianying
 */
public class SceneStatusModel implements Serializable {
    /**
     * 报表生成状态:0/就绪状态，1/生成中,2/完成生成
     */
    private Integer taskStatus;

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
