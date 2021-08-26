package io.shulie.takin.web.biz.pojo.response.linkmanage;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphEntity;
import com.pamirs.takin.entity.domain.dto.linkmanage.UnKnowNode;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.LinkEdge;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.LinkVertex;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/16 5:04 下午
 */
@Data
@ApiModel(value = "TechLinkResponse", description = "技术链路出参")
public class TechLinkResponse {

    @ApiModelProperty(name = "linkId", value = "系统流程id")
    private Long linkId;
    @ApiModelProperty(name = "techLinkName", value = "系统流程名字")
    private String techLinkName;
    @ApiModelProperty(name = "techLinkCount", value = "系统流程长度")
    private String techLinkCount;
    @ApiModelProperty(name = "isChange", value = "是否变更")
    private String isChange;
    @ApiModelProperty(name = "change_remark", value = "变更原因")
    private String change_remark;
    @ApiModelProperty(name = "body_before", value = "变更前")
    private String body_before;
    @ApiModelProperty(name = "body_after", value = "变更过后")
    private String body_after;
    @ApiModelProperty(name = "linkNode", value = "链路节点数据")
    private String linkNode;
    @ApiModelProperty(name = "changeType", value = "变更类型")
    private String changeType;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private java.util.Date createTime;
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private java.util.Date updateTime;
    @ApiModelProperty(name = "entrance", value = "入口")
    private String entrance;
    @ApiModelProperty(name = "candelete", value = "是否可以删除,有关联的业务活动的时候不可以删除" +
        ",没有关联的业务活动的时候可以删除,0:可以删除;1:不可以删除")
    private String candelete;
    @ApiModelProperty(name = "middleWare", value = "中间件集合")
    @JsonProperty("middleWare")
    private List<MiddleWareResponse> middleWareResponses;
    @ApiModelProperty(name = "topologicalGraphEntity", value = "变更前后拓扑图")
    private TopologicalGraphEntity topologicalGraphEntity;
    @ApiModelProperty(name = "requestParam", value = "入口参数")
    private String requestParam;
    @ApiModelProperty(name = "unKnowNodeList", value = "未知节点列表")
    private List<UnKnowNode> unKnowNodeList;
    private Map<Object, LinkVertex> linkVertexMap;
    private List<LinkEdge> edges;

}
