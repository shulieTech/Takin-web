package io.shulie.takin.web.biz.pojo.request.application;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: 南风
 * @Date: 2021/10/11 4:34 下午
 */
@Data
public class AppRemoteCallDelV2Request {

    @ApiModelProperty(name = "id", value = "记录id")
    @NotNull(message = "记录id不能为空")
    private Long id;
}
