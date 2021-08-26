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
    private String resultType;

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

}
