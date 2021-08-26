package com.pamirs.takin.entity.domain.vo;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TIsolationAppItemConfig;

public class TIsolationAppItemConfigVO extends TIsolationAppItemConfig {

    private List<Long> applicationIds;

    private List<Long> keyIds;

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public List<Long> getKeyIds() {
        return keyIds;
    }

    public void setKeyIds(List<Long> keyIds) {
        this.keyIds = keyIds;
    }
}
