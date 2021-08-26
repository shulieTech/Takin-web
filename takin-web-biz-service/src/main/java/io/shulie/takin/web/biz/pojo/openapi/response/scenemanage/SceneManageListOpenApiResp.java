package io.shulie.takin.web.biz.pojo.openapi.response.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@ApiModel(description = "列表查询出参")
@Data
public class SceneManageListOpenApiResp extends AuthQueryResponseCommonExt implements Serializable {

    @ApiModelProperty(
        name = "id",
        value = "ID"
    )
    private Long id;
    /**
     * 做兼容，部分客户已经用了customId
     */
    @ApiModelProperty("客户ID")
    private Long customId;
    /**
     * 做兼容，部分客户已经用了customName
     */
    @ApiModelProperty("客户名称")
    private String customName;
    @ApiModelProperty("场景名称")
    private String sceneName;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("最新压测时间")
    private String lastPtTime;
    @ApiModelProperty("是否有报告")
    private Boolean hasReport;
    @ApiModelProperty("预计消耗流量")
    private BigDecimal estimateFlow;
    @ApiModelProperty("最大并发")
    private Integer threadNum;
    @ApiModelProperty("拓展字段")
    private String features;

}
