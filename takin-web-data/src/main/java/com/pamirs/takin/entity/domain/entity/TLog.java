package com.pamirs.takin.entity.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;

/**
 * 说明：压测日志实体类
 *
 * @author shulie
 * @version V1.0
 * @date 2018年3月1日 下午12:49:55
 */
public class TLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //@Field logId : 日志id
    private long logId;

    //@Field principalNo : 负责人工号
    private String principalNo;

    // @Field linkId :链路id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long linkId;

    // @Field linkName : 链路名称
    private String linkName;

    // @Field applicationId : 应用id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long applicationId;

    // @Field applicationName : 应用名称
    private String applicationName;

    //@Field status : 操作状态(0表示失败,1表示成功)
    private int status;

    //@Field errorContent : 错误内容
    private String errorContent;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

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



    public TLog() {
        super();
    }

    /**
     * 2018年5月17日
     *
     * @return the logId
     * @author shulie
     * @version 1.0
     */
    public long getLogId() {
        return logId;
    }

    /**
     * 2018年5月17日
     *
     * @param logId the logId to set
     * @author shulie
     * @version 1.0
     */
    public void setLogId(long logId) {
        this.logId = logId;
    }

    /**
     * 2018年5月17日
     *
     * @return the principalNo
     * @author shulie
     * @version 1.0
     */
    public String getPrincipalNo() {
        return principalNo;
    }

    /**
     * 2018年5月17日
     *
     * @param principalNo the principalNo to set
     * @author shulie
     * @version 1.0
     */
    public void setPrincipalNo(String principalNo) {
        this.principalNo = principalNo;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkId
     * @author shulie
     * @version 1.0
     */
    public long getLinkId() {
        return linkId;
    }

    /**
     * 2018年5月17日
     *
     * @param linkId the linkId to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkName
     * @author shulie
     * @version 1.0
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * 2018年5月17日
     *
     * @param linkName the linkName to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    /**
     * 2018年5月17日
     *
     * @return the applicationId
     * @author shulie
     * @version 1.0
     */
    public long getApplicationId() {
        return applicationId;
    }

    /**
     * 2018年5月17日
     *
     * @param applicationId the applicationId to set
     * @author shulie
     * @version 1.0
     */
    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * 2018年5月17日
     *
     * @return the applicationName
     * @author shulie
     * @version 1.0
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 2018年5月17日
     *
     * @param applicationName the applicationName to set
     * @author shulie
     * @version 1.0
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 2018年5月17日
     *
     * @return the status
     * @author shulie
     * @version 1.0
     */
    public int getStatus() {
        return status;
    }

    /**
     * 2018年5月17日
     *
     * @param status the status to set
     * @author shulie
     * @version 1.0
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 2018年5月17日
     *
     * @return the errorContent
     * @author shulie
     * @version 1.0
     */
    public String getErrorContent() {
        return errorContent;
    }

    /**
     * 2018年5月17日
     *
     * @param errorContent the errorContent to set
     * @author shulie
     * @version 1.0
     */
    public void setErrorContent(String errorContent) {
        this.errorContent = errorContent;
    }

    /**
     * 2018年5月17日
     *
     * @return 实体字符串
     * @author shulie
     * @version 1.0
     */
    @Override
    public String toString() {
        return "TLog [logId=" + logId + ", principalNo=" + principalNo + ", linkId=" + linkId + ", linkName=" + linkName
            + ", applicationId=" + applicationId + ", applicationName=" + applicationName + ", status=" + status
            + ", errorContent=" + errorContent + "]";
    }
}
