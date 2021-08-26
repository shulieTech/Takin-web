package io.shulie.takin.web.biz.pojo.response.application;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.response.linkmanage.MiddleWareResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Data
@ApiModel(value = "ApplicationDetailResponse", description = "应用明细出参")
public class ApplicationDetailResponse implements Serializable {

    @ApiModelProperty(name = "middleWareResponses", value = "中间件列表")
    @JsonProperty("middleWare")
    List<MiddleWareResponse> middleWareResponses;
    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;
    @ApiModelProperty(name = "rpcType", value = "类型")
    private Integer rpcType;
    @ApiModelProperty(name = "rpcTypeTitle", value = "类型名称")
    private String rpcTypeTitle;
    @ApiModelProperty(name = "rpcData", value = "数据")
    private String rpcData;
    @ApiModelProperty(name = "nodes", value = "已知节点列表")
    private Set<String> nodes;
    @ApiModelProperty(name = "unKnowNodes", value = "未知节点列表")
    private Set<String> unKnowNodes;

}
