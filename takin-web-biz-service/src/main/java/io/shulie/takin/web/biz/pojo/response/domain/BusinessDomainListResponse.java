package io.shulie.takin.web.biz.pojo.response.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 5:02 下午
 * @Description:
 */
@Data
@ApiModel("业务域列表对象")
public class BusinessDomainListResponse {

    @ApiModelProperty("业务域ID")
    private Long id;

    @ApiModelProperty("业务域名称")
    private String name;

    @ApiModelProperty("业务域编码")
    private Integer domainCode;

}
