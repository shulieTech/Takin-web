package io.shulie.takin.web.biz.pojo.request.blacklist;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 3:37 下午
 */
@Data
public class BlacklistBatchEnableRequest {
    /**
     * 主键id
     */
    @ApiModelProperty(name = "ids", value = "黑名单id")
    @NotNull
    private List<Long> ids;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    @ApiModelProperty(name = "useYn", value = "(0表示未启动,1表示启动,2表示启用未校验)")
    @NotNull
    private Integer useYn;
}
