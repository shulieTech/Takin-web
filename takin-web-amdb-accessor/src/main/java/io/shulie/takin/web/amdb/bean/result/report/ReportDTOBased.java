package io.shulie.takin.web.amdb.bean.result.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ReportDTOBased implements Serializable {

    private Long reportId;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * rpcType
     */
    private String rpcType;
    /**
     * 最小耗时
     */
    private Long minCost;
    /**
     * 最大耗时
     */
    private Long maxCost;
    /**
     * 总耗时
     */
    private Long sumCost;
    /**
     * 请求数
     */
    private Long reqCnt;
    /**
     * 平均自耗时
     */
    private BigDecimal avgCost;
    /**
     * 创建时间
     */
    private Date gmtCreate;
}
