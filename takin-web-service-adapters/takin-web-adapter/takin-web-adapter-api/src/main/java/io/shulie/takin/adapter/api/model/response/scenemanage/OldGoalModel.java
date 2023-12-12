package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest.Goal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
    private String slaStartTime;

    /**
     * 自动设置SLA结束时间
     */
    @JSONField(name = "slaEndTime")
    private String slaEndTime;

    /**
     * 从{@link Goal}转换
     *
     * @param goal 新的目标实体
     * @return 旧的目标实体
     */
    public static OldGoalModel convert(Goal goal) {
        OldGoalModel oldGoalModel = new OldGoalModel();
        oldGoalModel.setTargetRt(goal.getRt());
        oldGoalModel.setTargetTps(goal.getTps());
        oldGoalModel.setTargetSa(BigDecimal.valueOf(goal.getSa()));
        oldGoalModel.setTargetSuccessRate(BigDecimal.valueOf(goal.getSr()));
        oldGoalModel.setSlaStartTime(goal.getSlaStartTime());
        oldGoalModel.setSlaEndTime(goal.getSlaEndTime());
        return oldGoalModel;
    }

    /**
     * 转换为{@link Goal}
     *
     * @return 新的目标实体
     */
    public Goal convert() {
        Goal goal = new Goal();
        goal.setRt(getTargetRt());
        goal.setTps(getTargetTps());
        goal.setSa(getTargetSa().doubleValue());
        goal.setSr(getTargetSuccessRate().doubleValue());
        goal.setSlaStartTime(getSlaStartTime());
        goal.setSlaEndTime(getSlaEndTime());
        return goal;
    }
}
