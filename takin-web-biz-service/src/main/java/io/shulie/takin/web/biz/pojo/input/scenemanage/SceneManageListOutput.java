package io.shulie.takin.web.biz.pojo.input.scenemanage;

import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/5 14:43
 */
@Data
public class SceneManageListOutput extends AuthQueryResponseCommonExt {
    @ApiModelProperty(name = "id", value = "ID")
    private Long id;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "最新压测时间")
    private String lastPtTime;

    @ApiModelProperty(value = "是否有报告")
    private Boolean hasReport;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(value = "最大并发")
    private Integer threadNum;

    @ApiModelProperty(value = "拓展字段")
    private String features;

    @ApiModelProperty(name = "tag", value = "场景标签")
    private List<TagManageResponse> tag;

    @ApiModelProperty(name = "isScheduler", value = "是否为定时启动")
    private Boolean isScheduler;

    @ApiModelProperty(name = "scheduleExecuteTime", value = "定时压测时间")
    private String scheduleExecuteTime;

    @ApiModelProperty(value = "是否存在脚本解析结果")
    private Boolean hasAnalysisResult;

    @ApiModelProperty(value = "压测进度")
    private Integer progress;

    @ApiModelProperty(name = "hasGlobalScene", value = "存在共享场景")
    private Boolean hasGlobalScene;
}
