package io.shulie.takin.web.data.result.application;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author fanxx
 * @date 2020/11/11 7:43 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
    private Long deptId;
    private String clusterName;
    private Integer confCheckVersion;
    /**
     * 0已下发，1已生效
     */
    private Integer confCheckStatus;
}
