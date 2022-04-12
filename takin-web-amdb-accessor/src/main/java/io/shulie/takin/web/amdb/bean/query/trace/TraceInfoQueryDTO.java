package io.shulie.takin.web.amdb.bean.query.trace;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Data
public class TraceInfoQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long startTime;

    private Long endTime;

    private Integer resultType;

    private List<String> entranceList;

    /**
     * 入口
     */
    private List<EntranceRuleDTO> entranceRuleDTOS;


    private Integer pageNum;

    private Integer pageSize;

    /**
     * 报告id
     */
    private Long reportId;

    private String traceId;

    /**
     * 耗时ms，比较规则 大于
     */
    private Long minCost;

    /**
     * 耗时ms，比较规则 小于等于
     */
    private Long maxCost;

    /**
     * 调用类型：middlewareName
     */
    private String middlewareName;

    /**
     * 接口名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 调用参数：request
     */
    private String request;

    /**
     * 排序字段：sortField （startDate、cost）
     */
    private String sortField;

    /**
     * 排序方式：sortType（asc、desc）
     */
    private String sortType;

    /**
     * 1-agent上报trace明细
     * 2-压测报告请求trace明细
     */
    private Integer queryType;
}
