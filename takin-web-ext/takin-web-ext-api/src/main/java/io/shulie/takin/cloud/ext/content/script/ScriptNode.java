package io.shulie.takin.cloud.ext.content.script;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.AbstractEntry;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liyuanba
 * @date 2021/10/26 11:29 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptNode extends AbstractEntry {
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点的testname属性内容
     */
    private String testName;
    /**
     * 元素节点的md5值
     */
    private String md5;
    /**
     * 类型
     */
    private NodeTypeEnum type;

    private NodeTypeEnum parentType;

    /**
     * 采样器类型
     */
    private SamplerTypeEnum samplerType;

    /**
     * 元素的绝对路劲
     */
    private String xpath;
    /**
     * xpath的md5
     */
    private String xpathMd5;
    /**
     * 属性信息
     */
    private Map<String, String> props;
    /**
     * 标识
     */
    private String identification;
    /**
     * 请求路径
     */
    private String requestPath;
    /**
     * 子节点
     */
    private List<ScriptNode> children;
}
