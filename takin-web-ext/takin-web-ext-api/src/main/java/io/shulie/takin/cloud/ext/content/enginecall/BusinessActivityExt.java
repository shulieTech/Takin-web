package io.shulie.takin.cloud.ext.content.enginecall;

import java.math.BigDecimal;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liyuanba
 * @date 2021/11/8 2:09 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessActivityExt extends AbstractEntry {
    /**
     * 绑定关系
     */
    private String bindRef;
    /**
     * 业务活动名称
     */
    private String activityName;
    /**
     * 业务指标，目标rt
     */
    private Integer rt;
    /**
     * 业务指标，目标tps
     */
    private Integer tps;
    /**
     * 业务目标tps占总的tps百分比
     */
    private Double rate;
    /**
     * 目标成功率
     */
    private BigDecimal sr;
    /**
     * 目标sa
     */
    private BigDecimal sa;
}
