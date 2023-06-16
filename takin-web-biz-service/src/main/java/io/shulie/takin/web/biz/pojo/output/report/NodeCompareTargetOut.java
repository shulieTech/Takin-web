package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("节点比对")
public class NodeCompareTargetOut implements Serializable {
    @ApiModelProperty("节点")
    private TopologyNode node;
    @ApiModelProperty("报告id")
    private List<Long> reportIds;

    @Data
    public static class TopologyNode {
        @ApiModelProperty("节点id")
        private String id;
        @ApiModelProperty("方法名")
        private String methodName;
        @ApiModelProperty("服务名")
        private String service;

        @ApiModelProperty("应用名称")
        protected String label;
        @ApiModelProperty("中间件名称")
        private String middlewareName;
        @ApiModelProperty("节点信息")
        private List<TopologyNode> nodes;
        @ApiModelProperty("服务Rt1")
        private Double service1Rt;
        @ApiModelProperty("服务Rt2")
        private Double service2Rt;
    }
}
