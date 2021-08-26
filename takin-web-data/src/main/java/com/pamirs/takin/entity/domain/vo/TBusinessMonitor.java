package com.pamirs.takin.entity.domain.vo;

import com.pamirs.takin.entity.domain.entity.TReport;

/**
 * 业务监控类
 *
 * @author shulie
 * @version v1.0
 * @2018年5月17日
 */
public class TBusinessMonitor extends TReport {
    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;
    //一级链路id
    private String firstLinkId;
    //一级链路名称
    private String firstLinkName;
    //报告id
    private Long tReportId;

    /**
     * 获取一级链路id
     *
     * @return 一级链路id
     */
    public String getFirstLinkId() {
        return firstLinkId;
    }

    /**
     * 设置一级链路id
     *
     * @param firstLinkId 一级链路id
     */
    public void setFirstLinkId(String firstLinkId) {
        this.firstLinkId = firstLinkId;
    }

    /**
     * 获取一级链路名称
     *
     * @return 一级链路名称
     */
    public String getFirstLinkName() {
        return firstLinkName;
    }

    /**
     * 设置一级链路名称
     *
     * @param firstLinkName 一级链路名称
     */
    public void setFirstLinkName(String firstLinkName) {
        this.firstLinkName = firstLinkName;
    }

    /**
     * 获取报告id
     *
     * @return 报告id
     */
    public Long gettReportId() {
        return tReportId;
    }

    /**
     * 设置报告id
     *
     * @param tReportId 报告id
     */
    public void settReportId(Long tReportId) {
        this.tReportId = tReportId;
    }
}
