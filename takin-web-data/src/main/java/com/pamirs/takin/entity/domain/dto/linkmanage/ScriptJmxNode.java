package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.shulie.takin.ext.content.emus.NodeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

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

    @ApiModelProperty(name = "businessActivityId", value = "业务活动id")
    private Long businessActivityId;

    @ApiModelProperty(name = "businessApplicationName", value = "业务活动应用名")
    private String businessApplicationName;

    @ApiModelProperty(name = "businessServicePath", value = "业务活动链接路径")
    private String businessServicePath;

    @ApiModelProperty(name = "children", value = "子节点")
    private List<ScriptJmxNode> children;


}
