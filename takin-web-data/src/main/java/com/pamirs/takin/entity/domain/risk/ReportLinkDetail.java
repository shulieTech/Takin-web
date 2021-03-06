package com.pamirs.takin.entity.domain.risk;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @ClassName: LinkDetailResult
 * @Package: com.pamirs.takin.web.api.service.risk.vo
 * @date 2020/7/2915:25
 */
@Data
@ApiModel
public class ReportLinkDetail implements Serializable {
    private String uuid;
    /**
     * 当前应用
     */
    @ApiModelProperty(value = "应用")
    private String appName;

    /**
     * 调用的接口信息/db/mq
     */
    @ApiModelProperty(value = "服务")
    private String serviceName;

    /**
     * 调用的接口类型
     */
    @ApiModelProperty(value = "类型")
    private String eventType;

    /**
     * 调用的接口信息/db/mq
     */
    private String event;

    /**
     * 总的请求数
     */
    @ApiModelProperty(value = "请求数")
    private Integer totalCount = 0;

    /**
     * 请求比
     */
    @ApiModelProperty(value = "请求比")
    private Double requestRate = 0D;

    /**
     * tps
     */
    @ApiModelProperty(value = "tps")
    private Double tps = 0D;

    /**
     * 最大RT
     */
    @ApiModelProperty(value = "最大RT")
    private String maxRt;

    /**
     * 最小rt
     */
    @ApiModelProperty(value = "最小RT")
    private String minRt;

    /**
     * 平均RT
     */
    @ApiModelProperty(value = "平均RT")
    private Double avgRt = 0D;

    /**
     * traceId
     */
    @ApiModelProperty(value = "traceId")
    private String traceId;

    @ApiModelProperty(value = "瓶颈标识")
    private Integer bottleneckFlag;

    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * 子链路
     */
    private List<ReportLinkDetail> children;
}
