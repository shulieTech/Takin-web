package io.shulie.takin.web.ext.entity.ecloud;

import lombok.Data;

import java.util.Date;

@Data
public class TenantPackageInfoExt {

    private Long tenantPackageId;

    private Integer maxVu;

    private Integer times;

    private Integer timesUsed;

    private Integer timesCurrent;

    private Integer packageType;

    private Integer status;

    private Date startDate;

    private Date endDate;

    private Integer duration;

    private Integer exclusiveEngine;
}
