package io.shulie.takin.web.biz.pojo.request.activity;

import javax.validation.constraints.NotNull;

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
public class ActivityDeleteRequest {

    @NotNull
    @ApiModelProperty("业务活动ID")
    private Long activityId;

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @ApiModelProperty("应用id")
    private Long applicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("应用类型")
    private EntranceTypeEnum type;

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;
}
