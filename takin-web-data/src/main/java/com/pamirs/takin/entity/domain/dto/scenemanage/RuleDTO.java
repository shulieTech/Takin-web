package com.pamirs.takin.entity.domain.dto.scenemanage;

import java.math.BigDecimal;

import com.pamirs.takin.entity.domain.vo.scenemanage.RuleVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/4/18 上午11:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RuleDTO extends RuleVO {

    private static final long serialVersionUID = 2831535102147741422L;

    public RuleDTO() {
    }

    public RuleDTO(Integer indexInfo, Integer condition, BigDecimal during, Integer times) {
        this.setIndexInfo(indexInfo);
        this.setCondition(condition);
        this.setDuring(during);
        this.setTimes(times);
    }
}
