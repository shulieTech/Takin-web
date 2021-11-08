package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotBlank;
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
@ApiModel("创建业务活动")
public class ActivityCreateRequest {

    @NotBlank
    @ApiModelProperty("业务活动名称")
    private String activityName;

    @NotBlank
    @ApiModelProperty("应用名称")
    private String applicationName;

    @NotNull
    @ApiModelProperty("入口类型")
    private EntranceTypeEnum type;

    @ApiModelProperty("链路id")
    private String linkId;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    @JsonProperty("link_level")
    private String activityLevel;

    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private Integer isCore;

    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

    @NotBlank
    private String method;

    @NotBlank
    private String rpcType;

    private String extend;

    @NotBlank
    private String serviceName;
}
