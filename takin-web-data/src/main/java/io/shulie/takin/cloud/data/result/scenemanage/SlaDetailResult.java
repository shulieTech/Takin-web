package io.shulie.takin.cloud.data.result.scenemanage;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/18 下午11:44
 */
@Data
public class SlaDetailResult {

    private String ruleName;

    private String businessActivity;

    private String rule;

    private EnumResult status;
}
