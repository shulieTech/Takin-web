package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 操作压测场景入参 -新
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "创建/修改 场景 - 新")
public class NewSceneRequest {
    /**
     * 接口id
     */
    private Long id;
    /**
     * 施压配置
     */
    private ThreadGroup threadConfig;
    /**
     * 单接口压测目标值
     */
    private SceneRequest.Goal targetGoal;

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
    private DataValidation dataValidation;

    @Getter
    @Setter
    public static class DataValidation extends SceneRequest.DataValidation {
        @ApiModelProperty("排除的应用id列表")
        private List<Long> excludedApplicationIds;
    }

    @Data
    public static class BasicInfo {
        @ApiModelProperty("场景主键")
        private Long sceneId;
        @ApiModelProperty(value = "压测场景名称")
        @NotBlank(message = "压测场景名称不能为空")
        private String name;
        @ApiModelProperty(value = "告警通知邮件")
        private String notifyEmails;
        @NotNull(message = "业务流程主键不能为空")
        @ApiModelProperty(value = "业务流程主键")
        private Long businessFlowId;
        @ApiModelProperty(value = "是否定时执行")
        @NotNull(message = "是否定时执行配置不能为空,0-手动 1-指定时间 2-周期执行")
        private int isScheduler;
        @ApiModelProperty(name = "executeTime", value = "定时执行时间")
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
        private Date executeTime;
        @ApiModelProperty(value = "定时执行表达式")
        private String executeCron;
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
        private String rampUpUnit = "m";
        @ApiModelProperty(value = "递增层次，施压模式为阶梯递增时的递增层数")
        private Integer steps;
        @ApiModelProperty(value = "预估流量")
        private Double estimateFlow;
    }

    @Data
    public static class ThreadGroup {
        @ApiModelProperty(value = "压力模式：0并发、1TPS、2自定义等")
        @NotNull(message = "压力模式不能为空")
        private Integer type = 0;
        @ApiModelProperty(value = "施压模式：1固定压力值，2线性递增，3阶梯递增")
        @NotNull(message = "压力模式不能为空")
        private Integer mode = 1;
        @ApiModelProperty(value = "并发线程数")
        private Integer threadNum = 1;
        @ApiModelProperty(value = "压测时间单位")
        private String unit = "m";
        @ApiModelProperty(value = "pod数")
        private Integer podNum = 1;
        @ApiModelProperty(value = "压测时间")
        private Long duration = 5L;
        @ApiModelProperty(value = "递增时长,施压模式为线性递增或阶梯递增时的递增时长")
        private Integer rampUp;
        @ApiModelProperty(value = "递增时长时间单位，s秒，m分，h小时")
        private String rampUpUnit;
        @ApiModelProperty(value = "递增层次，施压模式为阶梯递增时的递增层数")
        private Integer steps;
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

    public static NewSceneRequest DEFAULT = JSON.parseObject("{\n" +
                    "  \"basicInfo\": {\n" +
                    "    \"businessFlowId\": 241,\n" +
                    "    \"name\": \"DEFAULT\"\n" +
                    "  },\n" +
                    "  \"goal\": {\n" +
                    "    \"79c598b24f61e1f5a73600e6ffed1305\": {\n" +
                    "      \"tps\": 100,\n" +
                    "      \"rt\": 100,\n" +
                    "      \"sr\": 100,\n" +
                    "      \"sa\": 100\n" +
                    "    },\n" +
                    "    \"0ae306fb8361ee58e89b18b58d15c21c\": {\n" +
                    "      \"tps\": 100,\n" +
                    "      \"rt\": 100,\n" +
                    "      \"sr\": 100,\n" +
                    "      \"sa\": 100\n" +
                    "    },\n" +
                    "    \"b787744da465501175f19effa9b29855\": {\n" +
                    "      \"tps\": 100,\n" +
                    "      \"rt\": 100,\n" +
                    "      \"sr\": 100,\n" +
                    "      \"sa\": 100\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"config\": {\n" +
                    "    \"unit\": \"m\",\n" +
                    "    \"threadGroupConfigMap\": {\n" +
                    "      \"403e557d1984ce2fb6176e579b4e8830\": {\n" +
                    "        \"type\": 0,\n" +
                    "        \"threadNum\": 1,\n" +
                    "        \"mode\": 1\n" +
                    "      },\n" +
                    "      \"7dae7383a28b5c45069b528a454d1164\": {\n" +
                    "        \"type\": 0,\n" +
                    "        \"threadNum\": 1,\n" +
                    "        \"mode\": 1\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"podNum\": 1,\n" +
                    "    \"duration\": 5\n" +
                    "  },\n" +
                    "  \"destroyMonitoringGoal\": [\n" +
                    "    {\n" +
                    "      \"name\": \"DEFAULT\",\n" +
                    "      \"target\": [\n" +
                    "        \"0f1a197a2040e645dcdb4dfff8a3f960\"\n" +
                    "      ],\n" +
                    "      \"formulaTarget\": 0,\n" +
                    "      \"formulaSymbol\": 0,\n" +
                    "      \"formulaNumber\": 100000000,\n" +
                    "      \"numberOfIgnore\": 100000000\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"warnMonitoringGoal\": [],\n" +
                    "  \"dataValidation\": {\n" +
                    "    \"excludedApplicationIds\": [],\n" +
                    "    \"timeInterval\": 10\n" +
                    "  }\n" +
                    "}"
            ,
            new TypeReference<NewSceneRequest>() {
            });
}
