package io.shulie.takin.web.amdb.bean.query.script;

import java.util.List;

import io.shulie.takin.web.amdb.bean.common.PagingDevice;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脚本调试下的请求流量明细 dto
 *
 * @author liuchuan
 * @date 2021/5/13 2:02 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryLinkDetailDTO extends PagingDevice {

    /**
     * 结果类型, 0:失败  1:成功   2:断言失败
     * 整型
     */
    private Integer resultTypeInt;

    /**
     * 结果类型, 字符串
     */
    private Integer resultType;

    /**
     * 业务活动入口名称
     */
    private String serviceName;

    /**
     * cloud reportId
     */
    private String taskId;

    /**
     * 请求字段
     * 暂时默认 "appName,serviceName,methodName,middlewareName,rpcType"
     */
    private String fieldNames;

    /**
     * 入口列表, 字符串形式
     */
    private String entranceList;

    /**
     * 入口列表
     */
    private List<EntranceRuleDTO> entranceRuleDTOS;

    /**
     * 入口列表
     */
    private List<String> entrances;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 结束时间
     */
    private Integer clusterTest;

    /**
     * 用户编码
     */
    private String tenantAppKey;
    /**
     * 环境编码
     */
    private String envCode;

    private List<String> traceIdList;

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

    private String appName;

}
