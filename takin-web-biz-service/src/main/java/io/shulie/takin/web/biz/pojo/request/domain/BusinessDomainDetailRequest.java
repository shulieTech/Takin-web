package io.shulie.takin.web.biz.pojo.request.domain;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 业务域表(BusinessDomain)controller 入参类
 *
 * @author liuchuan
 * @date 2021-12-07 16:06:55
 */
@ApiModel("入参类-入参")
@Data
public class BusinessDomainDetailRequest {
    
    @ApiModelProperty("id")
    private Long id;
    
}
