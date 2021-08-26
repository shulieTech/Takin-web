package com.pamirs.takin.entity.domain.entity;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/6/9 下午5:22
 */
@Data
@ApiModel(value = "TBListDelete", description = "黑名单实体类")
public class TBListDelete {
    @ApiModelProperty(name = "blistIds", value = "黑名单编号列表")
    private List<Long> blistIds;
}
