package io.shulie.takin.web.biz.pojo.request.blacklist;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/6 2:22 下午
 */
@Data
public class BlacklistUpdateRequest {

    /**
     * 主键id
     */
    @ApiModelProperty(name = "blistId", value = "黑名单id")
    @NotNull
    private Long blistId;

    ///**
    // * 黑名单类型
    // */
    //@ApiModelProperty(name = "type", value = "黑名单类型")
    //private Integer type ;

    /**
     * 黑名单值
     */
    @ApiModelProperty(name = "redisKey", value = "黑名单值")
    @NotNull
    private String redisKey ;


    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    @NotNull
    private Long applicationId ;


}
