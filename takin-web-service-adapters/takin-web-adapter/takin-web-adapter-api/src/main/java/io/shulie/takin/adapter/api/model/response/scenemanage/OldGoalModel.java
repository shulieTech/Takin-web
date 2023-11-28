package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.Goal;
import io.swagger.annotations.ApiModelProperty;
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
     * 自动设置SLA开始时间
     */
    @JSONField(name = "slaStartTime")
    private Date slaStartTime;

    /**
     * 自动设置SLA结束时间
     */
    @JSONField(name = "slaEndTime")
    private Date slaEndTime;
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
            setSlaStartTime(goal.getSlaStartTime());
            setSlaEndTime(goal.getSlaEndTime());
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
            setSlaStartTime(getSlaStartTime());
            setSlaEndTime(getSlaEndTime());
        }};
    }
}
