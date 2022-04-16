package io.shulie.takin.cloud.biz.output.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共信息出参
 *
 * @author lipeng
 * @date 2021-06-24 3:57 下午
 */
@Data
@ApiModel("公共信息出参")
public class CommonInfosOutput {

    @ApiModelProperty("压测引擎版本号")
    private String pressureEngineVersion;

    @ApiModelProperty("cloud版本号")
    private String cloudVersion;

}
