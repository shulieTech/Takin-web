package io.shulie.takin.web.biz.pojo.output.report;

import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("节点比对")
public class NodeCompareTargetOut implements Serializable {
    @ApiModelProperty("节点列表")
    private List<ApplicationEntranceTopologyResponse.AbstractTopologyNodeResponse> nodes;

    @ApiModelProperty("业务活动id")
    private Long activityId;
    @ApiModelProperty("业务活动名称")
    private String activityName;
    @ApiModelProperty(name = "businessType", value = "业务活动类型，正常：0，虚拟：1")
    private int activityType;
    @ApiModelProperty(name = "节点信息", value = "key:reportId,value:节点信息")
    private Map<Long, List<NodeInfo>> nodeInfosMap;

    @Data
    public static class NodeInfo implements Serializable {
        @ApiModelProperty("应用名称")
        private String appName;
        @ApiModelProperty("应用rt")
        private double reportRt;
        private Long reportId;
    }
}
