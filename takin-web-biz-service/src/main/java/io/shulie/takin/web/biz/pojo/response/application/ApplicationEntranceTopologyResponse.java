package io.shulie.takin.web.biz.pojo.response.application;

import java.util.List;
import java.util.Set;

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
    public static abstract class AbstractTopologyNodeResponse {

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
    }

    @Data
    public static class TopologyAppNodeResponse extends AbstractTopologyNodeResponse {
        private String manager;
        private List<AppProviderInfo> providerService;
        private List<AppCallInfo> callService;
    }

    @Data
    public static class TopologyDbNodeResponse extends AbstractTopologyNodeResponse {
        private List<DbInfo> db;
    }

    @Data
    public static class TopologyCacheNodeResponse extends AbstractTopologyNodeResponse {
    }

    @Data
    public static class TopologyUnknownNodeResponse extends AbstractTopologyNodeResponse {
        private List<AppProviderInfo> providerService;
    }

    @Data
    public static class TopologyOtherNodeResponse extends AbstractTopologyNodeResponse {
        private List<AppProviderInfo> providerService;
    }

    @Data
    public static class TopologyVirtualNodeResponse extends AbstractTopologyNodeResponse {
        private List<AppProviderInfo> providerService;
    }

    @Data
    public static class TopologyMqNodeResponse extends AbstractTopologyNodeResponse {
        private List<AppProviderInfo> providerService;
        private List<MqInfo> mq;
    }

    @Data
    public static class TopologyOssNodeResponse extends AbstractTopologyNodeResponse {
        private List<OssInfo> oss;
    }

    @Data
    public static class NodeDetailDatasourceInfo {
        private String node;
    }

    @Data
    public static class AppProviderInfo {
        private String label;
        private List<AppProviderDatasourceInfo> dataSource;
    }

    @Data
    public static class AppProviderDatasourceInfo {
        private String serviceName;
        private String beforeApps;
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
