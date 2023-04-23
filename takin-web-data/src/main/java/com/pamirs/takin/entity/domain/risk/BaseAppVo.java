package com.pamirs.takin.entity.domain.risk;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author xingchen
 * @ClassName: BaseApp
 * @Package: com.pamirs.takin.web.api.service.risk.vo
 * @date 2020/7/2913:29
 */
@Data
public class BaseAppVo {
    private Long reportId;
    private String appName;
    private String appIp;
    private String agentIp;
    private Integer core;
    private BigDecimal memory;
    private BigDecimal disk;
    private BigDecimal mbps;
    private BigDecimal gcCount;
    private BigDecimal gcTime;
}
