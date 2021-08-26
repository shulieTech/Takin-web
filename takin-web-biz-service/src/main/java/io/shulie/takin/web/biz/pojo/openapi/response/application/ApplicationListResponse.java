package io.shulie.takin.web.biz.pojo.openapi.response.application;

import java.util.Date;
import java.util.List;
import java.io.Serializable;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-16 15:03
 */

@Data
@ApiModel(value = "ApplicationListResponse", description = "应用出参")
public class ApplicationListResponse implements Serializable {
    private static final long serialVersionUID = 412834588202338300L;

    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId;

    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    @ApiModelProperty(name = "accessStatus", value = "接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常")
    private Integer accessStatus;

    @ApiModelProperty(name = "nodeNum", value = "节点数量")
    private Integer nodeNum;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(name = "ipList", value = "ip地址列表")
    private List<String> ipsList;

}
