package io.shulie.takin.web.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 脚本节点数据
 * @author caijianying
 */
public class ScriptNodeSummaryModel implements Serializable {
    /**
     * 节点类型名称
     */
    private String name;
    /**
     * 节点名称
     */
    private String testName;
    /**
     * 业务活动ID
     */
    private Long activityId;
    /**
     * 节点类型
     */
    private String type;
    /**
     * TPS
     */
    private DataModel tps;
    /**
     * 平均RT
     */
    private DataModel avgRt;
    /**
     * 请求成功率
     */
    private DataModel successRate;
    /**
     * SA
     */
    private DataModel sa;
    /**
     * 最大tps
     */
    private BigDecimal maxTps;
    /**
     * 最大rt
     */
    private BigDecimal maxRt;
    /**
     * 最小rt
     */
    private BigDecimal minRt;
    /**
     * 总请求
     */
    private Long totalRequest;
    /**
     * 平均线程数
     */
    private BigDecimal avgConcurrenceNum;
    /**
     * 通过标识 1=通过 0=不通过
     */
    private Integer passFlag;
    /**
     * 子节点
     */
    private List<ScriptNodeSummaryModel> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataModel getTps() {
        return tps;
    }

    public void setTps(DataModel tps) {
        this.tps = tps;
    }

    public DataModel getAvgRt() {
        return avgRt;
    }

    public void setAvgRt(DataModel avgRt) {
        this.avgRt = avgRt;
    }

    public DataModel getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(DataModel successRate) {
        this.successRate = successRate;
    }

    public DataModel getSa() {
        return sa;
    }

    public void setSa(DataModel sa) {
        this.sa = sa;
    }

    public BigDecimal getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(BigDecimal maxTps) {
        this.maxTps = maxTps;
    }

    public BigDecimal getMaxRt() {
        return maxRt;
    }

    public void setMaxRt(BigDecimal maxRt) {
        this.maxRt = maxRt;
    }

    public BigDecimal getMinRt() {
        return minRt;
    }

    public void setMinRt(BigDecimal minRt) {
        this.minRt = minRt;
    }

    public Long getTotalRequest() {
        return totalRequest;
    }

    public void setTotalRequest(Long totalRequest) {
        this.totalRequest = totalRequest;
    }

    public BigDecimal getAvgConcurrenceNum() {
        return avgConcurrenceNum;
    }

    public void setAvgConcurrenceNum(BigDecimal avgConcurrenceNum) {
        this.avgConcurrenceNum = avgConcurrenceNum;
    }

    public Integer getPassFlag() {
        return passFlag;
    }

    public void setPassFlag(Integer passFlag) {
        this.passFlag = passFlag;
    }

    public List<ScriptNodeSummaryModel> getChildren() {
        return children;
    }

    public void setChildren(List<ScriptNodeSummaryModel> children) {
        this.children = children;
    }
}
