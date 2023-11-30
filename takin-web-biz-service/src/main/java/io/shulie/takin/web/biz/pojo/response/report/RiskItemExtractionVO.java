package io.shulie.takin.web.biz.pojo.response.report;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangz
 * Created on 2023/11/29 17:11
 * Email: zz052831@163.com
 */

@Data
public class RiskItemExtractionVO implements Serializable {
    private static final long serialVersionUID = -6720748025602729273L;
    /**
     * 风险项id
     */
    private Long riskItemId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 服务信息
     */
    private String targetUid;

    /**
     * 风险名称，可能是sla或者cause
     */
    private String riskName;

    /**
     * 风险收益
     */
    private Double profit;

    /**
     * 单挑收益
     */
    private Double singleProfit;

    /**
     * 修复成本
     */
    private Long repairCost;

    /**
     * 原因个数
     */
    private Integer causeCount;

    /**
     * 影响链路数
     */
    private Integer influenceChainCount;

    /**
     * 是否加入代办
     */
    private Boolean joinTodo;

    private Integer todoId;

    /**
     * 是否加入需求单
     */
    private Boolean joinDemandOrder;

    /**
     * 是否推荐
     */
    private Boolean recommend;

    private Boolean isParent;

    /**
     * 原因列表
     */
    private List<RiskItemExtractionVO> children;

    private Date gmtCreate;

    private Date gmtUpdate;
}
