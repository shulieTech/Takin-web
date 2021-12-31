package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScriptJmxNode {
    /**
     * 节点名称
     */
    private String name;

    /**
     * 元素节点的md5值
     */
    private String md5;

    /**
     * 属性信息
     */
    private Map<String, String> props;

    /**
     * 元素的绝对路劲
     */
    private String xpath;

    @ApiModelProperty(name = "type", value = "节点类型")
    private NodeTypeEnum type;

    @ApiModelProperty(name = "testName", value = "节点的testname属性内容")
    private String testName;

    @ApiModelProperty(name = "xpathMd5", value = "节点的唯一标识")
    private String xpathMd5;

    @ApiModelProperty(name = "identification", value = "url的拼接值")
    private String identification;

    @ApiModelProperty(name = "requestPath", value = "请求路径")
    private String requestPath;

    @ApiModelProperty(name = "samplerType", value = "采样器类型")
    private SamplerTypeEnum samplerType;

    @ApiModelProperty(name = "status", value = "状态：0未匹配完成，1匹配完成")
    private Integer status;

    @ApiModelProperty(name = "children", value = "子节点")
    private List<ScriptJmxNode> children;


    @ApiModelProperty(name = "businessActivityId", value = "业务活动id")
    private Long businessActivityId;

    @ApiModelProperty(name = "businessActivityName", value = "业务活动名称")
    private String businessActivityName;

    @ApiModelProperty(name = "businessApplicationName", value = "业务活动应用名")
    private String businessApplicationName;

    @ApiModelProperty("请求路径")
    private String serviceName;

    @ApiModelProperty("请求方式：GET,POST")
    private String method;

    @ApiModelProperty("远程调用类型")
    private String rpcType;

    @ApiModelProperty(name = "isChange", value = "是否有变更 0:正常；1:已变更")
    private Integer isChange;

    @ApiModelProperty(name = "isCore", value = "业务链路是否否核心链路 0:不是;1:是")
    private Integer isCore;

    @ApiModelProperty(name = "businessDomain", value = "业务域: 0:订单域\", \"1:运单域\", \"2:结算域")
    private String businessDomain;

    @ApiModelProperty(name = "activityLevel", value = "业务活动级别")
    private String activityLevel;

    @ApiModelProperty(name = "businessType", value = "业务活动类型")
    private Integer businessType;

    @ApiModelProperty(name = "bindBusinessId", value = "绑定业务活动")
    private Long bindBusinessId;

    @ApiModelProperty(name = "techLinkId", value = "技术链路ID")
    private String techLinkId;

    @ApiModelProperty(name = "entrace", value = "链路入口")
    private String entrace;

    @ApiModelProperty(name = "entracePath", value = "页面展示链路入口，不包含rpcType")
    private String entracePath;


}
