package io.shulie.takin.web.biz.pojo.request.blacklist;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-01
 */
@Data
public class BlacklistCreateRequest {

    @ApiModelProperty(name = "redisKey", value = "redisKey",required = true)
    @NotNull
    private String redisKey;
    @ApiModelProperty(name = "applicationId", value = "应用id",required = true)
    @NotNull
    private Long applicationId ;

}
