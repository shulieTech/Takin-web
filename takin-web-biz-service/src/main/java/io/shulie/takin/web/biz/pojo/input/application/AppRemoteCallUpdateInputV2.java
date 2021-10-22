package io.shulie.takin.web.biz.pojo.input.application;

import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallCreateV2Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: 南风
 * @Date: 2021/9/9 8:19 下午
 */
@Data
@NoArgsConstructor
public class AppRemoteCallUpdateInputV2 extends AppRemoteCallCreateV2Request {

    @ApiModelProperty(name = "id", value = "记录id")
    @NotNull(message = "记录id")
    private Long id;
}
