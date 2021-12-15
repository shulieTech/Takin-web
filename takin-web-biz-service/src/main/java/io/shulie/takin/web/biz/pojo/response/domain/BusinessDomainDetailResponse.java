package io.shulie.takin.web.biz.pojo.response.domain;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 业务域表(BusinessDomain)controller 详情响应类
 *
 * @author liuchuan
 * @date 2021-12-07 16:06:38
 */
@ApiModel("出参类-详情出参")
@Data
public class BusinessDomainDetailResponse {
    
    @ApiModelProperty("id")
    private Long id;
    
}
