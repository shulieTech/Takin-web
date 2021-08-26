package io.shulie.takin.web.biz.pojo.request.blacklist;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/6/9 下午5:22
 */
@Data
public class BlacklistDeleteRequest {
    @ApiModelProperty(name = "id", value = "黑名单id",required = true)
    @NotNull
    private Long id;
}
