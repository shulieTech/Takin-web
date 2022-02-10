package io.shulie.takin.web.api.model;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class SceneStatusModel implements Serializable {
    /**
     * 报表生成状态:0/就绪状态，1/生成中,2/完成生成
     */
    private Integer taskStatus;

    /**
     * 结论：1=通过 2=不通过
     */
    private Integer conclusion;

    /**
     * 不通过原因
     */
    private String conclusionRemark;

    public Integer getConclusion() {
        return conclusion;
    }

    public void setConclusion(Integer conclusion) {
        this.conclusion = conclusion;
    }

    public String getConclusionRemark() {
        return conclusionRemark;
    }

    public void setConclusionRemark(String conclusionRemark) {
        this.conclusionRemark = conclusionRemark;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
