package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/6/2 11:52 上午
 */
@Data
@ApiModel("创建虚拟业务活动")
public class VirtualActivityCreateRequest {

    @ApiModelProperty("业务活动名称")
    @NotEmpty(message = "业务活动名称不能为空")
    private String activityName;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    @JsonProperty("link_level")
    private String activityLevel;

    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private Integer isCore;

    @NotNull
    @ApiModelProperty("入口类型")
    private EntranceTypeEnum type;

    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

    @ApiModelProperty(name = "virtualEntrance", value = "虚拟入口")
    @NotEmpty(message = "虚拟入口不能为空")
    private String virtualEntrance;

    @ApiModelProperty(name = "bindBusinessId", value = "绑定业务活动id")
    private String bindBusinessId;

    @ApiModelProperty(name = "methodName", value = "请求方式：GET,PSOT")
    private String methodName;

    @ApiModelProperty(name= "category",value = "分类Id")
    private Long category;
}
