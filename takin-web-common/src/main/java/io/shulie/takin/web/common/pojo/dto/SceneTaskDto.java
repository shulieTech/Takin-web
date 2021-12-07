package io.shulie.takin.web.common.pojo.dto;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class SceneTaskDto extends TenantCommonExt {
    public SceneTaskDto(){

    }
    public SceneTaskDto(TenantCommonExt commonExt,Long reportId){
        this.setEnvCode(commonExt.getEnvCode());
        this.setTenantId(commonExt.getTenantId());
        this.setSource(commonExt.getSource());
        this.setTenantCode(commonExt.getTenantCode());
        this.setTenantAppKey(commonExt.getTenantAppKey());
        this.setReportId(reportId);
    }

    /**
     * 报告ID
     */
    private Long reportId;
}
