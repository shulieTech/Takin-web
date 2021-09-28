package com.pamirs.takin.entity.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;

/**
 * 说明: 抽取的链路关联表实体
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/12/26 15:52
 */
public abstract class AbstractRelationLinkModel extends BaseEntity {

    /**
     * 父链路id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    protected long parentLinkId;

    /**
     * 子链路id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    protected long childLinkId;

    /**
     * 横向链路编号
     */
    protected int linkOrder;

    /**
     * 纵向链路编号
     */
    protected int linkBank;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;

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

    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    public long getParentLinkId() {
        return parentLinkId;
    }

    public void setParentLinkId(long parentLinkId) {
        this.parentLinkId = parentLinkId;
    }

    public long getChildLinkId() {
        return childLinkId;
    }

    public void setChildLinkId(long childLinkId) {
        this.childLinkId = childLinkId;
    }

    public int getLinkOrder() {
        return linkOrder;
    }

    public void setLinkOrder(int linkOrder) {
        this.linkOrder = linkOrder;
    }

    public int getLinkBank() {
        return linkBank;
    }

    public void setLinkBank(int linkBank) {
        this.linkBank = linkBank;
    }

    @Override
    public String toString() {
        return "RelationLink{" +
                "parentLinkId=" + parentLinkId +
                ", childLinkId=" + childLinkId +
                ", linkOrder=" + linkOrder +
                ", linkBank=" + linkBank +
                '}';
    }
}
