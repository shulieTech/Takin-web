package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToLongFormatSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;

/**
 * 说明：压测报告实体类
 *
 * @author shulie
 * @version v1.0
 * @2018年5月17日
 */
public class TReport implements Serializable {

    private static final long serialVersionUID = 1334397427959885883L;

    // 主键id
    private Long id;

    // 二级链路id
    private String secondLinkId;

    //二级链路的tps
    private Integer secondLinkTps;

    // 业务链路名称
    private String linkServiceName;

    // 基础链路名称
    private String linkBasicName;

    // 基础链路实体
    private String linkBasic;

    // 压测开始时间
    @JsonSerialize(using = DateToLongFormatSerialize.class)
    private Date startTime;

    // 压测结束时间
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date endTime;

    // rt达标标准
    private Integer rtStandard;

    // 报告状态 0:未压测;1:压测中;2:压测完成
    private Integer status;

    // 创建时间 默认：CURRENT_TIMESTAMP
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    // 更新时间 默认：CURRENT_TIMESTAMP
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date modifyTime;

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    private long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
    /**
     * 2018年5月17日
     *
     * @return the id
     * @author shulie
     * @version 1.0
     */
    public Long getId() {
        return id;
    }

    /**
     * 2018年5月17日
     *
     * @param id the id to set
     * @author shulie
     * @version 1.0
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 2018年5月17日
     *
     * @return the secondLinkId
     * @author shulie
     * @version 1.0
     */
    public String getSecondLinkId() {
        return secondLinkId;
    }

    /**
     * 2018年5月17日
     *
     * @param secondLinkId the secondLinkId to set
     * @author shulie
     * @version 1.0
     */
    public void setSecondLinkId(String secondLinkId) {
        this.secondLinkId = secondLinkId;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkServiceName
     * @author shulie
     * @version 1.0
     */
    public String getLinkServiceName() {
        return linkServiceName;
    }

    /**
     * 2018年5月17日
     *
     * @param linkServiceName the linkServiceName to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkServiceName(String linkServiceName) {
        this.linkServiceName = linkServiceName;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkBasicName
     * @author shulie
     * @version 1.0
     */
    public String getLinkBasicName() {
        return linkBasicName;
    }

    /**
     * 2018年5月17日
     *
     * @param linkBasicName the linkBasicName to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkBasicName(String linkBasicName) {
        this.linkBasicName = linkBasicName;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkBasic
     * @author shulie
     * @version 1.0
     */
    public String getLinkBasic() {
        return linkBasic;
    }

    /**
     * 2018年5月17日
     *
     * @param linkBasic the linkBasic to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkBasic(String linkBasic) {
        this.linkBasic = linkBasic;
    }

    /**
     * 2018年5月17日
     *
     * @return the startTime
     * @author shulie
     * @version 1.0
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 2018年5月17日
     *
     * @param startTime the startTime to set
     * @author shulie
     * @version 1.0
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 2018年5月17日
     *
     * @return the endTime
     * @author shulie
     * @version 1.0
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 2018年5月17日
     *
     * @param endTime the endTime to set
     * @author shulie
     * @version 1.0
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 2018年5月17日
     *
     * @return the rtStandard
     * @author shulie
     * @version 1.0
     */
    public Integer getRtStandard() {
        return rtStandard;
    }

    /**
     * 2018年5月17日
     *
     * @param rtStandard the rtStandard to set
     * @author shulie
     * @version 1.0
     */
    public void setRtStandard(Integer rtStandard) {
        this.rtStandard = rtStandard;
    }

    /**
     * 2018年5月17日
     *
     * @return the status
     * @author shulie
     * @version 1.0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 2018年5月17日
     *
     * @param status the status to set
     * @author shulie
     * @version 1.0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 2018年5月17日
     *
     * @return the createTime
     * @author shulie
     * @version 1.0
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 2018年5月17日
     *
     * @param createTime the createTime to set
     * @author shulie
     * @version 1.0
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 2018年5月17日
     *
     * @return the modifyTime
     * @author shulie
     * @version 1.0
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 2018年5月17日
     *
     * @param modifyTime the modifyTime to set
     * @author shulie
     * @version 1.0
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getSecondLinkTps() {
        return secondLinkTps;
    }

    public void setSecondLinkTps(Integer secondLinkTps) {
        this.secondLinkTps = secondLinkTps;
    }
}
