package io.shulie.takin.cloud.data.result.scenemanage;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/18 下午8:36
 */
@Data
public class BusinessActivityDetailResult {

    private transient Long businessActivityId;

    private String businessActivityName;

    private Integer targetTPS;

    private Integer targetRT;

    private BigDecimal targetSuccessRate;

    private BigDecimal targetSA;

}
