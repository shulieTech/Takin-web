package io.shulie.takin.web.api.model;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class SceneGoalModel implements Serializable {

    /**
     * 业务活动ID
     */
    private Long businessActivityId;

    /**
     * 业务活动名称
     */
    private String businessActivityName;

    /**
     * 目标TPS
     */
    private Integer tps;
    /**
     * 目标RT(ms)
     */
    private Integer rt;
    /**
     * 目标成功率(%)
     */
    private Double sr;
    /**
     * 目标SA(%)
     */
    private Double sa;

    public Long getBusinessActivityId() {
        return businessActivityId;
    }

    public void setBusinessActivityId(Long businessActivityId) {
        this.businessActivityId = businessActivityId;
    }

    public String getBusinessActivityName() {
        return businessActivityName;
    }

    public void setBusinessActivityName(String businessActivityName) {
        this.businessActivityName = businessActivityName;
    }

    public Integer getTps() {
        return tps;
    }

    public void setTps(Integer tps) {
        this.tps = tps;
    }

    public Integer getRt() {
        return rt;
    }

    public void setRt(Integer rt) {
        this.rt = rt;
    }

    public Double getSr() {
        return sr;
    }

    public void setSr(Double sr) {
        this.sr = sr;
    }

    public Double getSa() {
        return sa;
    }

    public void setSa(Double sa) {
        this.sa = sa;
    }



}
