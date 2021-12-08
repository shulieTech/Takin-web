package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.Map;
import java.util.Date;
import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneRequest;

/**
 * 操作压测场景入参 -新
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "创建/修改 场景 - 新")
public class NewSceneRequest {
    @ApiModelProperty(value = "基础信息")
    @NotNull(message = "场景基础信息不能为空")
    private BasicInfo basicInfo;
    @ApiModelProperty(value = "施压配置")
    @NotNull(message = "施压配置不能为空")
    private PtConfig config;
    @ApiModelProperty(value = "压测目标")
    @NotNull(message = "业压测目标不能为空,key节点的xpathMd5，value是配置信息")
    private Map<String, SceneRequest.Goal> goal;
    @ApiModelProperty(value = "SLA配置-终止")
    @NotNull(message = "【SLA配置-终止】不能为空")
    private List<MonitoringGoal> destroyMonitoringGoal;
    @ApiModelProperty(value = "SLA配置-警告")
    @NotNull(message = "【SLA配置-警告】不能为空")
    private List<MonitoringGoal> warnMonitoringGoal;
    @ApiModelProperty(value = "数据验证配置")
    @NotNull(message = "数据验证配置不能为空")
    private SceneRequest.DataValidation dataValidation;

    @Data
    public static class BasicInfo {
        @ApiModelProperty("场景主键")
        private Long sceneId;
        @ApiModelProperty(value = "压测场景名称")
        @NotBlank(message = "压测场景名称不能为空")
        private String name;
        @NotNull(message = "业务流程主键不能为空")
        @ApiModelProperty(value = "业务流程主键")
        private Long businessFlowId;
        @ApiModelProperty(value = "是否定时执行")
        @NotNull(message = "是否定时执行配置不能为空")
        private Boolean isScheduler;
        @ApiModelProperty(name = "executeTime", value = "定时执行时间")
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
        private Date executeTime;
    }

    @Data
    public static class PtConfig {
        @ApiModelProperty(value = "启动的POD数")
        @NotNull(message = "POD数不能为空")
        private Integer podNum;
        @ApiModelProperty(value = "压测时长")
        @NotNull(message = "压测时长不能为空")
        private Long duration;
        @ApiModelProperty(value = "压测时长单位")
        @NotNull(message = "压测时长单位")
        private String unit;
        @ApiModelProperty(value = "预估流量")
        private Double estimateFlow;
        @ApiModelProperty(value = "线程组内施压配置，key为xpathMd5,value为具体配置")
        private Map<String, ThreadGroupConfig> threadGroupConfigMap;
    }

    @Data
    public static class ThreadGroupConfig {
        @ApiModelProperty(value = "压力模式：0并发、1TPS、2自定义等")
        @NotNull(message = "压力模式不能为空")
        private Integer type;
        @ApiModelProperty(value = "施压模式：1固定压力值，2线性递增，3阶梯递增")
        @NotNull(message = "压力模式不能为空")
        private Integer mode;
        @ApiModelProperty(value = "并发线程数")
        private Integer threadNum;
        @ApiModelProperty(value = "递增时长,施压模式为线性递增或阶梯递增时的递增时长")
        private Integer rampUp;
        @ApiModelProperty(value = "递增时长时间单位，s秒，m分，h小时")
        private String rampUpUnit;
        @ApiModelProperty(value = "递增层次，施压模式为阶梯递增时的递增层数")
        private Integer steps;
        @ApiModelProperty(value = "预估流量")
        private Double estimateFlow;
    }

    /**
     * 监控目标
     */
    @Data
    @ApiModel(value = "监控目标")
    public static class MonitoringGoal {
        @ApiModelProperty(value = "名称")
        @NotBlank(message = "名称不能为空")
        private String name;
        @ApiModelProperty(value = "对象(MD5值)")
        @NotNull(message = "对象不能为空")
        private List<String> target;
        @ApiModelProperty(value = "算式目标")
        @NotNull(message = "条件规则指标不能为空")
        private Integer formulaTarget;
        @ApiModelProperty(value = "算式符号")
        @NotNull(message = "条件规则判断条件不能为空")
        private Integer formulaSymbol;
        @ApiModelProperty(value = "算式数值")
        @NotNull(message = "条件规则判断数据不能为空")
        private Double formulaNumber;
        @ApiModelProperty(value = "忽略次数")
        @NotNull(message = "连续出现次数不能为空")
        private Integer numberOfIgnore;
    }
}
