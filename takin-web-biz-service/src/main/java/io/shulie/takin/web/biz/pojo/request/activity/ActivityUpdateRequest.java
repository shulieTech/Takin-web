package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("业务活动修改对象")
public class ActivityUpdateRequest {

    @ApiModelProperty("optType")
    private String optType;

    @NotNull
    @ApiModelProperty("业务活动ID")
    private Long activityId;

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @ApiModelProperty("应用名称")
    @JsonProperty("applicationId")// 目前下拉框的应用名和id一样，兼容一下
    private String applicationName;

    @ApiModelProperty("应用类型")
    private EntranceTypeEnum type;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    @JsonProperty("link_level")
    private String activityLevel;

    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private Integer isCore;

    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    @ApiModelProperty(name = "category", value = "分类Id")
    private Long category;
}
