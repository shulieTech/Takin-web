package io.shulie.takin.web.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author caijianying
 */
public class SceneStatusModel implements Serializable {
    /**
     * 状态值 0=已结束 1=压测中 2=启动压测失败
     */
    private Long data;

    private Long reportId;

    private List<String> msg;

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }
}
