package io.shulie.takin.web.common.pojo.dto;

import java.time.LocalDateTime;
import java.util.Date;

import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class SceneTaskDto extends TenantCommonExt {
    public SceneTaskDto(){

    }
    public SceneTaskDto(TenantCommonExt commonExt, Long reportId, Date startTime){
        this.setEnvCode(commonExt.getEnvCode());
        this.setTenantId(commonExt.getTenantId());
        this.setSource(commonExt.getSource());
        this.setTenantCode(commonExt.getTenantCode());
        this.setTenantAppKey(commonExt.getTenantAppKey());
        this.setReportId(reportId);
        this.setStartTime(startTime);
    }

    public SceneTaskDto(Long reportId, ContextSourceEnum source, Date startTime, LocalDateTime endTime){
        if (null == reportId ){
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,"报告ID不能为空！");
        }
        this.setEnvCode(WebPluginUtils.traceEnvCode());
        this.setTenantId(WebPluginUtils.traceTenantId());
        this.setSource(source.getCode());
        this.setTenantCode(WebPluginUtils.traceTenantCode());
        this.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        this.setReportId(reportId);
        this.setEndTime(endTime);
        this.setStartTime(startTime);
    }

    /**
     * 报告ID
     */
    private Long reportId;

    private LocalDateTime endTime;

    private Date startTime;
}
