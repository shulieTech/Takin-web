package io.shulie.takin.web.biz.pojo.request.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 5:01 下午
 * @Description:
 */
@Data
@ApiModel("业务域批量删除对象")
public class BusinessDomainDeleteRequest {

    @NotEmpty
    @ApiModelProperty("业务域对象id集合")
    private List<Long> ids;
}
