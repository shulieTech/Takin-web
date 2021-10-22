package io.shulie.takin.web.biz.pojo.response.application;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.shulie.amdb.common.dto.link.topology.LinkEdgeDTO;
import io.shulie.amdb.common.enums.NodeTypeGroupEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("入口服务拓扑图返回值")
public class ApplicationEntranceTopologyResponse {

    @ApiModelProperty("节点")
    private List<AbstractTopologyNodeResponse> nodes;

    @ApiModelProperty("边")
    private List<ApplicationEntranceTopologyEdgeResponse> edges;

    private List<ExceptionListResponse> exceptions;

    public enum NodeTypeResponseEnum {
        APP("app"),
        CACHE("cache"),
        MQ("mq"),
        DB("db"),
        OSS("oss"),
        SEARCH("search"),
        OUTER("outer"),
        UNKNOWN("unknown"),
        VIRTUAL("virtual");
        @Getter
        private String type;

        NodeTypeResponseEnum(String type) {
            this.type = type;
        }

        public static NodeTypeResponseEnum getTypeByAmdbType(String groupEnum) {
            NodeTypeResponseEnum[] enumConstants = NodeTypeResponseEnum.class.getEnumConstants();
            for (NodeTypeResponseEnum enumConstant : enumConstants) {
                if (enumConstant.getType().equalsIgnoreCase(groupEnum)) {
                    return enumConstant;
                }
            }
            return NodeTypeResponseEnum.UNKNOWN;
        }

        public static NodeTypeResponseEnum getTypeByAmdbType(NodeTypeGroupEnum groupEnum) {
            NodeTypeResponseEnum[] enumConstants = NodeTypeResponseEnum.class.getEnumConstants();
            for (NodeTypeResponseEnum enumConstant : enumConstants) {
                if (enumConstant.getType().equalsIgnoreCase(groupEnum.getType())) {
                    return enumConstant;
                }
            }
            return NodeTypeResponseEnum.UNKNOWN;
        }
    }

    @Data
    public static class AbstractTopologyNodeResponse {

        @ApiModelProperty("节点id")
        protected String id;

        @ApiModelProperty("节点显示")
        protected String label;

        @ApiModelProperty("是否根节点")
        protected Boolean root;

        @ApiModelProperty("节点类型")
        protected NodeTypeResponseEnum nodeType;

        @ApiModelProperty("上游应用名称")
        private List<String> upAppNames;

        @ApiModelProperty("节点信息")
        private List<NodeDetailDatasourceInfo> nodes;

        // <<<<< 节点中 某个服务的 4 个性能指标
            @ApiModelProperty("服务总调用量")
            private Double serviceAllTotalCount;

            @ApiModelProperty("服务总成功率")
            private Double serviceAllSuccessRate;

            @ApiModelProperty("服务总Tps")
            private Double serviceAllTotalTps;

            @ApiModelProperty("服务Rt")
            private Double serviceRt;
        // >>>>> 节点中 某个服务的 4 个性能指标

        // 一般瓶颈 [false 没有瓶颈 | true 有一般瓶颈]
        boolean hasL1Bottleneck = false;
        // 一般瓶颈数量
        int l1bottleneckNum = 0;

        // 严重瓶颈 [false 没有瓶颈 | true 有严重瓶颈]
        boolean hasL2Bottleneck = false;
        // 严重瓶颈数量
        int l2bottleneckNum = 0;

        // TopologyAppNodeResponse
        private String manager;
        private List<AppProviderInfo> providerService;
        private List<AppCallInfo> callService;

        // TopologyDbNodeResponse
        private List<DbInfo> db;

        // TopologyMqNodeResponse
        private List<MqInfo> mq;

        // TopologyOssNodeResponse
        private List<OssInfo> oss;
    }

    @Data
    public static class TopologyAppNodeResponse extends AbstractTopologyNodeResponse {
    }

    @Data
    public static class TopologyDbNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyCacheNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyUnknownNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyOtherNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyVirtualNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyMqNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologySearchNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class TopologyOssNodeResponse extends TopologyAppNodeResponse {
    }

    @Data
    public static class NodeDetailDatasourceInfo {
        private String node;
    }

    @Data
    public static class AppProviderInfo {
        // MiddlewareName
        private String label;
        private List<AppProvider> dataSource;
    }

    @Data
    public static class AppProvider {
        List<LinkEdgeDTO> containEdgeList;
        List<AppProvider> containRealAppProvider;

        // 所属应用
        private String ownerApps;
        // 调用服务 的中间件类型
        private String middlewareName;
        // 服务名称
        private String serviceName;
        // 上游应用
        private String beforeApps;
        private HashMap<String, String> beforeAppsMap;
        // 服务开关状态(true 打开 | false 关闭)
        private Boolean switchState;


        // 边 id
        private String eagleId;
        // 源节点ID
        private String source;
        // 目标节点ID
        private String target;

        // 边 rpcType
        private String rpcType;

    // <<<<< 节点中 某个服务的 4 个性能指标
        // 1) 总调用量
        private Double serviceAllTotalCount;

        // 2) 总成功率
        private Double serviceAllSuccessRate;
            // 总成功调用次数
            private Double allSuccessCount;
            // 总成功率 瓶颈类型(-1 没有瓶颈 | 1 一般瓶颈 | 2 严重瓶颈)
            private int allSuccessRateBottleneckType;
            // 总成功率 瓶颈详情 id
            private Long successRateBottleneckId;

        // 3) 总Tps
        private Double serviceAllTotalTps;

        // 4) 前端显示 RT
        private Double serviceRt;
            // 平均RT
            private Double serviceAvgRt;
            // 总调用Rt
            private Double allTotalRt;
                // 总Rt 瓶颈类型(-1 没有瓶颈 | 1 一般卡慢 | 2 严重卡慢)
                private int allTotalRtBottleneckType;
                // 总Rt 瓶颈详情 id
                private Long rtBottleneckId;
            // 最大RT
            private Double serviceAllMaxRt;
                // 总Rt sql 瓶颈类型(-1 没有瓶颈 | 1 一般卡慢 | 2 严重卡慢)
                private int allSqlTotalRtBottleneckType;
                // 总Rt sql 瓶颈详情 id
                private Long rtSqlBottleneckId;
    // >>>>> 节点中 某个服务的 4 个性能指标
    }

    @Data
    public static class AppCallInfo {
        private String label;
        private NodeTypeResponseEnum nodeType;
        private List<AppCallDatasourceInfo> dataSource;
    }

    @Data
    public static class AppCallDatasourceInfo {
        private String label;
        private List<String> dataSource;
    }

    @Data
    public static class DbInfo {
        private String tableName;
    }

    @Data
    public static class MqInfo {
        private String topic;
    }

    @Data
    public static class OssInfo {
        private String fileName;
        private String filePath;
    }

    @Data
    @ApiModel("入口服务拓扑图边返回值")
    public static class ApplicationEntranceTopologyEdgeResponse {

        @ApiModelProperty("源节点ID")
        private String source;

        @ApiModelProperty("目标节点ID")
        private String target;

        @ApiModelProperty("节点ID")
        private String label;

        @ApiModelProperty("边访问类型")
        private String type;

        @ApiModelProperty("边上面的信息")
        private String info;

        @ApiModelProperty("合并后边上面的信息")
        private Set<String> infos;

        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("总调用量")
        private Double allTotalCount;

        @ApiModelProperty("主干")
        private boolean isMain;
    }

    @Data
    public static class ExceptionListResponse {

        @ApiModelProperty("异常描述")
        private String title;

        @ApiModelProperty("异常类型")
        private String type;

        @ApiModelProperty("建议解决方案")
        private String suggest;
    }

}
