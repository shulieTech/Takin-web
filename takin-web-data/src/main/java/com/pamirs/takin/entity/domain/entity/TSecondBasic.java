package com.pamirs.takin.entity.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO.PI;

/**
 * 说明: 二级链路基础链路关联关系表实体类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/6/18 10:30
 */
public class TSecondBasic extends BaseEntity {

    // @Field secondLinkId : 二级链路id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long secondLinkId;

    // @Field basicLinkId : 基础链路id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long basicLinkId;

    // @Field blinkOrder : 基础链路顺序
    private int blinkOrder;

    // @Field blinkBank : 基础链路等级(二级链路下有基础链路1，基础链路2等)
    private int blinkBank;
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

    public TSecondBasic() {
    }

    public long getSecondLinkId() {
        return secondLinkId;
    }

    public void setSecondLinkId(long secondLinkId) {
        this.secondLinkId = secondLinkId;
    }

    public long getBasicLinkId() {
        return basicLinkId;
    }

    public void setBasicLinkId(long basicLinkId) {
        this.basicLinkId = basicLinkId;
    }

    public int getBlinkOrder() {
        return blinkOrder;
    }

    public void setBlinkOrder(int blinkOrder) {
        this.blinkOrder = blinkOrder;
    }

    public int getBlinkBank() {
        return blinkBank;
    }

    public void setBlinkBank(int blinkBank) {
        this.blinkBank = blinkBank;
    }

    @Override
    public String toString() {
        return "TSecondBasic{" +
            "secondLinkId=" + secondLinkId +
            ", basicLinkId=" + basicLinkId +
            ", blinkOrder=" + blinkOrder +
            ", blinkBank=" + blinkBank +
            '}';
    }
}
