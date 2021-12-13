package io.shulie.takin.web.biz.pojo.request.domain;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 5:01 下午
 * @Description:
 */
@Data
@ApiModel("业务域更新对象")
public class BusinessDomainUpdateRequest {

    @NotNull
    @ApiModelProperty("业务域ID")
    private Long id;

    @ApiModelProperty("业务域名称")
    private String name;
}
