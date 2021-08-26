package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/24 21:50
 */
@Data
@ApiModel(value = "BusinessActiveViewListDto", description = "业务活动列表出参")
public class BusinessActiveViewListDto extends AuthQueryResponseCommonExt {
    @ApiModelProperty(name = "businessActiceId", value = "业务活动主键")
    private String businessActiceId;
    @ApiModelProperty(name = "businessActiveName", value = "业务活动名字")
    private String businessActiveName;
    @ApiModelProperty(name = "ischange", value = "是否变更")
    private String ischange;
    @ApiModelProperty(name = "middleWareList", value = "中间件集合")
    private List<String> middleWareList;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private java.util.Date createTime;
    @ApiModelProperty(name = "candelete", value = "是否可以删除")
    private String candelete;
    @ApiModelProperty(name = "systemProcessName", value = "系统流程名字")
    private String systemProcessName;
    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

}
