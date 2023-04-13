package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建/修改 场景  -  请求
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "创建/修改 场景")
@EqualsAndHashCode(callSuper = true)
public class SceneRequest extends ContextExt {
    @ApiModelProperty(value = "基础信息")
    @NotNull(message = "场景基础信息不能为空")
    private BasicInfo basicInfo;
    @ApiModelProperty(value = "脚本解析结果")
    @NotNull(message = "脚本解析结果不能为空")
    private List<ScriptNode> analysisResult;
    @ApiModelProperty(value = "压测内容")
    @NotNull(message = "压测内容不能为空")
    private List<Content> content;
    @ApiModelProperty(value = "施压配置")
    @NotNull(message = "施压配置不能为空")
    private PtConfigExt config;
    @ApiModelProperty(value = "压测目标")
    @NotNull(message = "压测目标不能为空")
    private Map<String, Goal> goal;
    @ApiModelProperty(value = "SLA配置")
    @NotNull(message = "SLA配置不能为空")
    private List<MonitoringGoal> monitoringGoal;
    @ApiModelProperty(value = "数据验证配置")
    @NotNull(message = "数据验证配置不能为空")
    private DataValidation dataValidation;
    @ApiModelProperty(value = "压测文件")
    @NotNull(message = "压测文件不能为空")
    private List<File> file;

    /**
     * 基础信息
     */
    @Data
    public static class BasicInfo {
        @ApiModelProperty(value = "场景主键")
        private Long sceneId;
        @ApiModelProperty(value = "场景名称")
        @NotBlank(message = "场景名称不能为空")
        private String name;
        @ApiModelProperty(value = "场景类型:0常规，3流量调试，4巡检，5试跑模式")
        @NotNull(message = "场景类型不能为空")
        private Integer type;
        @ApiModelProperty(value = "脚本实例主键")
        @NotNull(message = "脚本实例主键不能为空")
        private Long scriptId;
        @ApiModelProperty(value = "脚本类型")
        @NotNull(message = "脚本类型不能为空")
        private Integer scriptType;
        @ApiModelProperty(value = "业务流程主键")
        @NotNull(message = "业务流程主键不能为空")
        private Long businessFlowId;
    }

    /**
     * 压测内容
     */
    @Data
    @ApiModel(value = "压测内容")
    public static class Content {
        @ApiModelProperty(value = "名称")
        @NotBlank(message = "名称不能为空")
        private String name;
        @ApiModelProperty(value = "脚本节点路径MD5")
        @NotBlank(message = "MD5不能为空")
        private String pathMd5;
        @ApiModelProperty(value = "业务活动主键")
        @NotNull(message = "业务活动主键不能为空")
        private Long businessActivityId;
        @ApiModelProperty(value = "关联应用的主键")
        private List<String> applicationId;
    }

    /**
     * 压测目标
     */
    @Data
    @ApiModel(value = "压测目标")
    public static class Goal {
        @ApiModelProperty(value = "目标TPS")
        private Integer tps;
        @ApiModelProperty(value = "目标RT(ms)")
        private Integer rt;
        @ApiModelProperty(value = "目标成功率(%)")
        private Double sr;
        @ApiModelProperty(value = "目标SA(%)")
        private Double sa;

        public static Goal buildNewGoal(Goal origGoal) {
            Goal newGoal = (origGoal == null ? new Goal() : origGoal);
            if(newGoal.getTps() == null) {
                newGoal.setTps(100);
            }
            if(newGoal.getRt() == null) {
                newGoal.setRt(100);
            }
            if(newGoal.getSr() == null) {
                newGoal.setSr(80d);
            }
            if(newGoal.getSa() == null) {
                newGoal.setSa(80d);
            }
            return newGoal;
        }
    }

    /**
     * 监控目标
     */
    @Data
    @ApiModel(value = "监控目标")
    public static class MonitoringGoal {
        /**
         * 自增主键，回显使用
         */
        private Long id;
        @NotNull(message = "监控类型不能为空")
        @ApiModelProperty(value = "监控类型 终止/告警")
        private Integer type;
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

    /**
     * 数据验证配置
     */
    @Data
    @ApiModel(value = "数据验证配置")
    public static class DataValidation {
        @ApiModelProperty(value = "时间间隔")
        @NotNull(message = "时间间隔不能为空")
        private Integer timeInterval;
        @ApiModelProperty(value = "内容-不明")
        private String content;
    }

    /**
     * 压测文件
     */
    @Data
    public static class File {
        @ApiModelProperty(value = "文件路径")
        @NotBlank(message = "文件路径不能为空")
        private String path;
        @ApiModelProperty(value = "文件名称")
        @NotBlank(message = "文件名称不能为空")
        private String name;
        @ApiModelProperty(value = "文件签名")
        private String sign;
        @ApiModelProperty(value = "文件类型")
        @NotNull(message = "文件类型不能为空")
        private Integer type;
        @ApiModelProperty(value = "文件拓展信息")
        @NotNull(message = "文件拓展信息不能为空")
        Map<String, Object> extend;
    }
}
