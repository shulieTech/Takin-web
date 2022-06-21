package io.shulie.takin.adapter.api.model.response.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共信息返回报文
 *
 * @author lipeng
 * @date 2021-06-24 4:08 下午
 */
@Data
@ApiModel("公共信息返回报文")
public class CommonInfosResp {

    @ApiModelProperty("压测引擎版本号")
    private String engineVersion;

    @ApiModelProperty("cloud版本号")
    private String version;
}
