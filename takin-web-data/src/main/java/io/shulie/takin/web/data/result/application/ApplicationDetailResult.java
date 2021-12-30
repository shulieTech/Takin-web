package io.shulie.takin.web.data.result.application;

import java.util.Date;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/11/11 7:43 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationDetailResult extends TenantCommonExt {
    private Long id;

    private Long applicationId;
    private String applicationName;
    private String applicationDesc;
    private String ddlScriptPath;
    private String cleanScriptPath;
    private String readyScriptPath;
    private String basicScriptPath;
    private String cacheScriptPath;
    private Long cacheExpTime;
    private Integer useYn;
    private String agentVersion;
    private Integer nodeNum;
    private Integer accessStatus;
    private String switchStatus;
    private String exceptionInfo;
    private Date createTime;
    private Date updateTime;
    private String alarmPerson;
    private String pradarVersion;
    private Long userId;
    private String md5;
}
