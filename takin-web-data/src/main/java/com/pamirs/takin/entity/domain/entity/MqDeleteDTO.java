package com.pamirs.takin.entity.domain.entity;

import java.util.Date;
import java.util.List;

public class MqDeleteDTO {

    private List<Long> ids;

    private Date updateTime;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
