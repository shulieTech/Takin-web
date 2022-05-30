package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.Goal;
import lombok.Data;

/**
 * 旧的压测目标模型
 *
 * @author 张天赐
 */
@Data
public class OldGoalModel {
    /**
     * 成功率
     */
    @JSONField(name = "successRate")
    private BigDecimal targetSuccessRate;
    /**
     * 响应时间
     */
    @JSONField(name = "RT")
    private Integer targetRt;
    /**
     * 达标率
     */
    @JSONField(name = "SA")
    private BigDecimal targetSa;
    /**
     * TPS
     */
    @JSONField(name = "TPS")
    private Integer targetTps;

    /**
     * 从{@link Goal}转换
     *
     * @param goal 新的目标实体
     * @return 旧的目标实体
     */
    public static OldGoalModel convert(Goal goal) {
        return new OldGoalModel() {{
            setTargetRt(goal.getRt());
            setTargetTps(goal.getTps());
            setTargetSa(BigDecimal.valueOf(goal.getSa()));
            setTargetSuccessRate(BigDecimal.valueOf(goal.getSr()));
        }};
    }

    /**
     * 转换为{@link Goal}
     *
     * @return 新的目标实体
     */
    public Goal convert() {
        return new Goal() {{
            setRt(getTargetRt());
            setTps(getTargetTps());
            setSa(getTargetSa().doubleValue());
            setSr(getTargetSuccessRate().doubleValue());
        }};
    }
}
