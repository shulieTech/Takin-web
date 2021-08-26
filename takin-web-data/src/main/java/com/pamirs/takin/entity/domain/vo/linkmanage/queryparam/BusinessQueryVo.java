package com.pamirs.takin.entity.domain.vo.linkmanage.queryparam;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/2 10:39
 */
@Data
@ApiModel(value = "BusinessQueryVo", description = "业务链路页面查询vo")
public class BusinessQueryVo extends PagingDevice implements Serializable {
    private static final long serialVersionUID = 4891597070201989044L;

    @ApiModelProperty(name = "businessLinkName", value = "业务链路名字")
    private String businessLinkName;
    @ApiModelProperty(name = "techLinkName", value = "技术链路名字")
    private String techLinkName;
    @ApiModelProperty(name = "entrance", value = "链路入口")
    private String entrance;
    @ApiModelProperty(name = "ischange", value = "是否变更")
    private String ischange;
    @ApiModelProperty(name = "domain", value = "业务域")
    private String domain;
    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;
    @ApiModelProperty(name = "middleWareName", value = "中间件名称")
    private String middleWareName;
    @ApiModelProperty(name = "version", value = "中间件版本号")
    private String version;
   /* @ApiModelProperty(name = "relatedLinkId", value = "业务链路关联的技术链路id")
    private String relatedLinkId;*/
}
