package io.shulie.takin.web.data.result.risk;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

/**
 * @author xingchen
 * @ClassName: LinkDataResult
 * @Package: com.pamirs.takin.web.api.service.risk.vo
 * @date 2020/7/2915:25
 */
@Data
public class LinkDataResult {
    private Long reportId;

    /**
     * 当前应用
     */
    private String appName;

    /**
     * 调用的接口信息/db/mq
     */
    private String serviceName;

    /**
     * 调用的接口类型
     */
    private String eventType;

    /**
     * 调用的接口信息/db/mq
     */
    private String event;

    /**
     * tps
     */
    private Double tps = 0D;

    private Double minTps = 0D;

    private Double maxTps = 0D;

    /**
     * rt
     */
    private Double rt = 0D;

    private Double minRt = 0D;

    private Double maxRt = 0D;

    /**
     * 错误数
     */
    private Integer errorCount = 0;

    /**
     * 同级计算权重值
     */
    private BigDecimal calcWeight;

    /**
     * 实际权重值（要和子节点分摊）
     */
    private BigDecimal realWeight;

    /**
     * 所在节点总数
     */
    private Integer nodeCount;

    /**
     * 子链路
     */
    private List<LinkDataResult> subLink = Lists.newArrayList();
}
