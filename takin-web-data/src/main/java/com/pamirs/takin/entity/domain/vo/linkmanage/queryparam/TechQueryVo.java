package com.pamirs.takin.entity.domain.vo.linkmanage.queryparam;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/2 10:35
 */
@Data
@ApiModel(value = "TechQueryVo", description = "技术链路查询入参")
public class TechQueryVo extends PagingDevice implements Serializable {

    @ApiModelProperty(name = "linkName", value = "链路名")
    private String linkName;

    @ApiModelProperty(name = "entrance", value = "入口")
    private String entrance;

    @ApiModelProperty(name = "ischange", value = "是否变更")
    private String ischange;

    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;

    @ApiModelProperty(name = "middleWareName", value = "中间件名称")
    private String middleWareName;

    @ApiModelProperty(name = "middleWareVersion", value = "中间件版本")
    private String middleWareVersion;
}
