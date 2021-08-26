package io.shulie.takin.web.biz.pojo.openapi.response.linkmanage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shulie
 */
@Data
@ApiModel(value = "SystemProcessViewListOpenApiResp", description = "系统流程列表出参数")
public class SystemProcessViewListOpenApiResp implements Serializable {
    @ApiModelProperty(name = "linkId", value = "系统流程id")
    private Long linkId;
    @ApiModelProperty(name = "techLinkName", value = "系统流程名字")
    private String techLinkName;
    @ApiModelProperty(name = "techLinkCount", value = "系统流程长度")
    private String techLinkCount;
    @ApiModelProperty(name = "isChange", value = "是否变更")
    private String isChange;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private java.util.Date createTime;
    @ApiModelProperty(name = "candelete", value = "是否可以删除,有关联的业务活动的时候不可以删除" +
        ",没有关联的业务活动的时候可以删除,0:可以删除;1:不可以删除")
    private String candelete;
    @ApiModelProperty(name = "middleWareEntities", value = "中间件集合列表")
    private List<String> middleWareEntities;
    @ApiModelProperty(name = "changeType", value = "变更事情")
    private String changeType;

}
