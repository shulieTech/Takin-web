package io.shulie.takin.web.biz.pojo.response.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 场景列表数据模型
 *
 * @author mubai
 * @date 2020-12-01 16:00
 */

@Data
public class SceneListResponse extends AuthQueryResponseCommonExt implements Serializable {
    private static final long serialVersionUID = 3772247948183081636L;

    @ApiModelProperty(name = "id", value = "ID")
    private Long id;

    /**
     * 做兼容，部分客户已经用了customId
     */
    @ApiModelProperty(value = "客户ID")
    private Long customId;
    /**
     * 做兼容，部分客户已经用了customName
     */
    @ApiModelProperty(value = "客户名称")
    private String customName;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    //当为定时压测场景时保存定时执行时间
    @ApiModelProperty(value = "最新压测时间")
    private String lastPtTime;

    @ApiModelProperty(name = "scheduleExecuteTime", value = "定时压测时间")
    private String scheduleExecuteTime;

    @ApiModelProperty(value = "是否有报告")
    private Boolean hasReport;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(value = "最大并发")
    private Integer threadNum;

    @ApiModelProperty(value = "拓展字段")
    private String features;

    @ApiModelProperty(name = "isScheduler", value = "是否为定时启动")
    private Boolean isScheduler;

    @ApiModelProperty(name = "tag", value = "场景标签")
    private List<TagManageResponse> tag;

}
