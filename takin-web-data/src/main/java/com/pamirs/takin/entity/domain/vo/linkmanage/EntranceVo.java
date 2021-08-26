package com.pamirs.takin.entity.domain.vo.linkmanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/11/30 16:56
 */
@Data
@ApiModel(value = "EntranceApiVo", description = "入口入参")
public class EntranceVo implements Serializable {

    @ApiModelProperty(name = "ischange", value = "单条业务流程是否变更")
    private String ischange;
    @ApiModelProperty(name = "relatedBusinessId", value = "业务活动id")
    private String relatedBusinessId;
    @ApiModelProperty(name = "relateTechId", value = "系统流程id")
    private String relateTechId;
    @ApiModelProperty(name = "入口", value = "系统流程id")
    private String entrance;

    @ApiModelProperty(name = "parentBusinessId", value = "针对业务流程配置的上级业务活动id")
    private String parentBusinessId;

}
