package io.shulie.takin.web.data.param.baseserver;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-10-26 17:20
 */
@Data
public class ProcessOverRiskParam {

    private List<String> appNames;

    private Long sceneId;

    private Long reportId;

    private BigDecimal destTps;

}
