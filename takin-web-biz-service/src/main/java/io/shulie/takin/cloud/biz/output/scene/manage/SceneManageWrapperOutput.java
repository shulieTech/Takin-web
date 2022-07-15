package io.shulie.takin.cloud.biz.output.scene.manage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.common.bean.scenemanage.SceneBusinessActivityRefBean;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/4/17 下午5:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "场景详情出参")
public class SceneManageWrapperOutput extends ContextExt {

    @ApiModelProperty(value = "压测场景ID")
    private Long id;

    @ApiModelProperty(value = "客户ID")
    private Long customId;

    @ApiModelProperty(value = "场景负责人")
    private Long managerId;

    @ApiModelProperty(value = "压测场景名称")
    private String pressureTestSceneName;

    @ApiModelProperty(value = "业务活动配置")
    private List<SceneBusinessActivityRefOutput> businessActivityConfig;

    @ApiModelProperty(value = "线程组施压配置")
    private Map<String, ThreadGroupConfigExt> threadGroupConfigMap;

    /**
     * 施压场景,0:常规;4试跑；5巡检；不填默认为0
     * 为了兼容老的，所以1，2,3也是常规
     */
    @ApiModelProperty(value = "施压场景,0:常规,1:常规,2:常规;3常规；4试跑；5巡检；不填默认为0")
    private Integer pressureType;

    @ApiModelProperty(value = "并发数量")
    private Integer concurrenceNum;

    @ApiModelProperty(value = "指定IP数")
    private Integer ipNum;

    @ApiModelProperty(value = "压测时长(秒)")
    private Long pressureTestSecond;

    @ApiModelProperty(value = "压测时长")
    private TimeBean pressureTestTime;

    //    @ApiModelProperty(value = "施压模式")
    //    @NotNull(message = "施压模式不能为空")
    //    private Integer pressureMode;
    //
    //    @ApiModelProperty(value = "递增时长(秒)")
    //    private Long increasingSecond;
    //
    //    @ApiModelProperty(value = "递增时长")
    //    private TimeBean increasingTime;
    //
    //    @ApiModelProperty(value = "阶梯层数")
    //    private Integer step;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(name = "scriptType", value = "脚本类型")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    private List<SceneScriptRefOutput> uploadFile;

    @ApiModelProperty(name = "stopCondition", value = "SLA终止配置")
    private List<SceneSlaRefOutput> stopCondition;

    @ApiModelProperty(name = "warningCondition", value = "SLA警告配置")
    private List<SceneSlaRefOutput> warningCondition;

    @ApiModelProperty(name = "status", value = "压测状态")
    private Integer status;

    @ApiModelProperty(value = "总测试时长(压测时长+预热时长)")
    private transient Long totalTestTime;

    private transient String updateTime;

    private transient String lastPtTime;

    /**
     * 最后压测时间
     */
    private Date lastPtDateTime;

    private String features;

    private String scriptAnalysisResult;

    private Integer configType;

    private Long scriptId;

    private Long businessFlowId;

    private Integer scheduleInterval;

    private List<Long> enginePluginIds;

    private List<EnginePluginRefOutput> enginePlugins;

    private Integer loopsNum;

    private Long fixedTimer;

    private boolean isInspect;

    private boolean isTryRun;

    private boolean continueRead;

    /**
     * 场景类型:0:普通场景，1:流量调试
     */
    private Integer type;

    private StrategyConfigExt strategy;

    private String resourceId;

    private String ptConfig;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SceneBusinessActivityRefOutput extends SceneBusinessActivityRefBean {

        @ApiModelProperty(value = "ID")
        private Long id;

        @ApiModelProperty(value = "绑定关系")
        private String bindRef;

        @ApiModelProperty(value = "应用IDS")
        private String applicationIds;

        private Long scriptId;

        private String businessFlowId;
        /**
         * 是否包含压测头
         */
        private Boolean hasPT;

    }

    @Data
    public static class SceneSlaRefOutput {

        @ApiModelProperty(value = "ID")
        private Long id;

        @ApiModelProperty(value = "规则名称")
        private String ruleName;

        @ApiModelProperty(value = "适用对象")
        private String[] businessActivity;

        @ApiModelProperty(value = "规则")
        private RuleBean rule;

        @ApiModelProperty(value = "状态")
        private Integer status;

        @ApiModelProperty(value = "触发事件")
        private String event;
    }

    @Data
    public static class SceneScriptRefOutput {

        @ApiModelProperty(value = "ID")
        private Long id;

        @ApiModelProperty(value = "文件名称")
        private String fileName;

        @ApiModelProperty(value = "文件类型")
        private Integer fileType;

        @ApiModelProperty(value = "文件大小")
        private String fileSize;

        @ApiModelProperty(value = "上传时间")
        private String uploadTime;

        @ApiModelProperty(value = "上传路径")
        private String uploadPath;

        @ApiModelProperty(value = "是否删除")
        private Integer isDeleted;

        @ApiModelProperty(value = "上传数据量")
        private Long uploadedData;

        @ApiModelProperty(value = "是否拆分")
        private Integer isSplit;

        @ApiModelProperty(value = "Topic")
        private String topic;

        @ApiModelProperty(value = "是否按顺序拆分")
        private Integer isOrderSplit;

        @ApiModelProperty(value = "是否大文件")
        private Integer isBigFile;

        @ApiModelProperty(value = "文件MD5值")
        private String fileMd5;
    }

    @Data
    public static class EnginePluginRefOutput {
        @ApiModelProperty(value = "插件ID")
        private Long id;
        @ApiModelProperty(value = "插件版本")
        private String version;

        public static EnginePluginRefOutput create(Long id, String version) {
            EnginePluginRefOutput output = new EnginePluginRefOutput();
            output.setId(id);
            output.setVersion(version);
            return output;
        }
    }

}
