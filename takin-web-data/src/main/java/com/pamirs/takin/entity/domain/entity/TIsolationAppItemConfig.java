package com.pamirs.takin.entity.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;

public class TIsolationAppItemConfig extends BaseEntity {

    /**
     * 明细ID
     */
    private Long itemId;

    /**
     * 主表ID
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private Long applicationId;

    /**
     * 明细类型
     */
    private String type;

    /**
     * 注册中心ID、Rocket集群名称
     */
    private Long keyId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getKeyId() {
        return keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }
}
