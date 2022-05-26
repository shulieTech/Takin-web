package io.shulie.takin.cloud.common.bean.sla;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qianshui
 * @date 2020/4/20 下午5:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchieveModel {

    private Integer times = 0;

    private Long lastAchieveTime;

}
