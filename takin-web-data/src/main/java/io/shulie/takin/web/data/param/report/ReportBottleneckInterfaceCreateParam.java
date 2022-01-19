package io.shulie.takin.web.data.param.report;

import java.math.BigDecimal;

import lombok.Data;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/11/23 10:52
 */
@Data
public class ReportBottleneckInterfaceCreateParam {

    private Long reportId;

    private String applicationName;

    private Integer sortNo;

    /**
     * 接口类型
     */

    private String interfaceType;

    private String interfaceName;

    private BigDecimal tps;

    private BigDecimal rt;

    private Integer nodeCount;

    private Integer errorReqs;

    private BigDecimal bottleneckWeight;
}
