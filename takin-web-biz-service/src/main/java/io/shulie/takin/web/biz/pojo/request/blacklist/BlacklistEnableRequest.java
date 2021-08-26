package io.shulie.takin.web.biz.pojo.request.blacklist;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 3:37 下午
 */
@Data
public class BlacklistEnableRequest {
    /**
     * 主键id
     */
    @ApiModelProperty(name = "blistId", value = "黑名单id")
    @NotNull
    private Long blistId;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    @ApiModelProperty(name = "useYn", value = "(0表示未启动,1表示启动,2表示启用未校验)")
    @NotNull
    private Integer useYn;
}
