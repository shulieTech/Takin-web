package io.shulie.takin.web.biz.pojo.request.domain;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 5:01 下午
 * @Description:
 */
@Data
@ApiModel("业务域创建对象")
public class BusinessDomainCreateRequest {

    @NotBlank
    @ApiModelProperty("业务域名称")
    private String name;
}
