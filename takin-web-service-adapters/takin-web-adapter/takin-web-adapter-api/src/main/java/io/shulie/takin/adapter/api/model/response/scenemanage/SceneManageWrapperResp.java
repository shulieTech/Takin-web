package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.common.SceneBusinessActivityRefBean;
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
public class SceneManageWrapperResp extends ContextExt {

    @ApiModelProperty(value = "压测场景ID")
    private Long id;

    @ApiModelProperty(value = "压测场景名称")
    private String pressureTestSceneName;

    @ApiModelProperty(value = "业务活动配置")
    private List<SceneBusinessActivityRefResp> businessActivityConfig;

    @ApiModelProperty(value = "施压类型,0:并发,1:tps,2:自定义;不填默认为0")
    private Integer pressureType;

    @ApiModelProperty(value = "并发数量")
    private Integer concurrenceNum;

    @ApiModelProperty(value = "指定IP数")
    private Integer ipNum;

    @ApiModelProperty(value = "压测时长(秒)")
    private Long pressureTestSecond;

    @ApiModelProperty(value = "压测时长")
    private TimeBean pressureTestTime;

    @ApiModelProperty(value = "施压模式")
    @NotNull(message = "施压模式不能为空")
    private Integer pressureMode;

    @ApiModelProperty(value = "递增时长(秒)")
    private Long increasingSecond;

    @ApiModelProperty(value = "递增时长")
    private TimeBean increasingTime;

    @ApiModelProperty(value = "阶梯层数")
    private Integer step;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(name = "scriptType", value = "脚本类型")
    private Integer scriptType;

    @ApiModelProperty(name = "uploadFile", value = "压测脚本/文件")
    private List<SceneScriptRefResp> uploadFile;

    @ApiModelProperty(name = "stopCondition", value = "SLA终止配置")
    private List<SceneSlaRefResp> stopCondition;

    @ApiModelProperty(name = "warningCondition", value = "SLA警告配置")
    private List<SceneSlaRefResp> warningCondition;

    @ApiModelProperty(name = "status", value = "压测状态")
    private Integer status;

    @ApiModelProperty(value = "总测试时长(压测时长+预热时长)")
    private transient Long totalTestTime;

    private transient String updateTime;

    private transient String lastPtTime;

    private String features;

    private String scriptAnalysisResult;

    private Integer configType;

    private Long scriptId;

    private String businessFlowId;

    @ApiModelProperty(name = "executeTime", value = "定时执行时间")
    private String executeTime;

    @ApiModelProperty(name = "tag", value = "场景标签")
    private List<String> tag;

    @ApiModelProperty(name = "isScheduler", value = "是否为定时启动场景")
    private Boolean isScheduler;

    @ApiModelProperty(name = "scheduleInterval", value = "漏数时间间隔")
    private Integer scheduleInterval;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SceneBusinessActivityRefResp extends SceneBusinessActivityRefBean {

        @ApiModelProperty(value = "ID")
        private Long id;

        @ApiModelProperty(value = "绑定关系")
        private String bindRef;

        @ApiModelProperty(value = "应用IDS")
        private String applicationIds;

        private Long scriptId;

    }

    @Data
    public static class SceneSlaRefResp {

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
    public static class SceneScriptRefResp {

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
    }

}
