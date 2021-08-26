package com.pamirs.takin.entity.domain.entity.linkmanage;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/7 11:15
 */
@Data
@ApiModel(value = "MiddleWareDistinctVo", description = "MiddleWareDistinctVo")
public class MiddleWareDistinctVo implements Serializable {
    @ApiModelProperty(name = "ids", value = "业务活动主键集合")
    private List<String> ids;
}
