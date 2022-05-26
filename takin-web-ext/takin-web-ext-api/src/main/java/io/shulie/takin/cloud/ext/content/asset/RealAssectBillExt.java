package io.shulie.takin.cloud.ext.content.asset;

import java.math.BigDecimal;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 真实压测流量计算请求对象
 *
 * @author liyuanba
 * @date 2021/11/5 11:49 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RealAssectBillExt extends AbstractEntry {
    /**
     * 平均并发线程数
     */
    private BigDecimal avgThreadNum;
    /**
     * 压测时长，单位秒
     */
    private Long time;
}
