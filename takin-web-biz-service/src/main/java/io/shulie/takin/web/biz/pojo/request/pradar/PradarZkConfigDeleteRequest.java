package io.shulie.takin.web.biz.pojo.request.pradar;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("pradar配置删除")
public class PradarZkConfigDeleteRequest {

    @NotNull(message = "配置ID" + AppConstants.MUST_BE_NOT_NULL)
    @ApiModelProperty("配置ID")
    private Long id;

}
