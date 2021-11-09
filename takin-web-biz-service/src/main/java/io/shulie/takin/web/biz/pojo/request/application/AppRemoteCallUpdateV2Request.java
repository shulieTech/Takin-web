package io.shulie.takin.web.biz.pojo.request.application;

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
public class AppRemoteCallUpdateV2Request extends AppRemoteCallCreateV2Request {

    @ApiModelProperty(name = "id", value = "记录id")
    private Long id;

    /**
     * 是否是手动录入的
     */
    @ApiModelProperty(name = "isManual", value = "是否手动录入")
    private Boolean isManual = false;
}
