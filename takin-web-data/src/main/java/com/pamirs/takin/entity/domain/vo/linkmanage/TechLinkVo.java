package com.pamirs.takin.entity.domain.vo.linkmanage;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/11/29 14:59
 */
@Data
@ApiModel(value = "TechLinkVo", description = "技术链路入参")
public class TechLinkVo implements Serializable {

    @ApiModelProperty(name = "linkId", value = "系统流程id")
    private String linkId;
    @NotNull
    @ApiModelProperty(name = "entrance", value = "入口")
    private String entrance;
    @NotNull
    @ApiModelProperty(name = "linkName", value = "链路名")
    private String linkName;
    @ApiModelProperty(name = "body", value = "流程消息体")
    private String body;
    //中间件集合
    @ApiModelProperty(name = "middleWareLists", value = "中间件集合")
    private List<MiddleWareEntity> middleWareLists;

}
