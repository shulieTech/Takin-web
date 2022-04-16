package io.shulie.takin.cloud.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author moriarty
 *
 * {@link JsonPath } 处理json字符串
 */
@Slf4j
public class JsonPathUtil {

    /**
     * 默认需要删除的节点，用以处理脚本的树状结构，删除无用的节点
     */
    public static final List<String> DEFAULT_REGEXPS = Collections.singletonList("$..props");

    /**
     * 默认匹配节点的key
     */
    public static final String DEFAULT_KEY = "xpathMd5";

    /**
     * 默认解析配置
     * jsonProvider 使用 {@link JacksonJsonProvider}
     * mappingProvider 使用 {@link JacksonMappingProvider}
     * options 默认总是返回{@link List}
     */
    public static final Configuration JACKSON_CONFIGURATION = Configuration.builder()
        .jsonProvider(new JacksonJsonProvider())
        .mappingProvider(new JacksonMappingProvider())
        .options(Option.ALWAYS_RETURN_LIST)
        .build();

    public static final TypeRef<List<ScriptNode>> SCRIPT_NODE_TYPE_REF = new TypeRef<List<ScriptNode>>() {};

    /**
     * 删除json字符串中的节点
     *
     * @param jsonString 目标json字符串
     * @return json文档对象
     */
    public static DocumentContext deleteNodes(String jsonString) {
        return deleteNodes(jsonString, DEFAULT_REGEXPS);
    }

    /**
     * 删除json字符串中的节点，
     *
     * @param jsonString json字符串
     * @param regexps    需要删除的节点表达式
     *                   例如：删除所有的props：表达式为：$..props;
     *                   删除当前节点的name：表达式为 @.name
     *                   可以使用过滤条件进行过滤：
     *                   删除所有price大于10的节点：表达式为： $..[?(@.price > 10)]
     *                   过滤条件为字符串的表达式：$..[?(@.name='moriarty')]
     * @return json文档对象
     */
    public static DocumentContext deleteNodes(String jsonString, List<String> regexps) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        DocumentContext context = null;
        try {
            context = JsonPath.parse(jsonString);
            DocumentContext finalContext = context;
            regexps.forEach(r -> finalContext.delete(JsonPath.compile(r)));
            return finalContext;
        } catch (Exception e) {
            log.error("json delete regex error!json={}", jsonString);
        }
        return context;
    }

    /**
     * 向json中添加节点
     *
     * @param context  json文档对象
     * @param key      需要添加节点的父节点名称
     * @param nodeMaps 需要添加的节点；外层Key为匹配父节点名称的value，内层map：key是json的key，value是json的value
     */
    public static void putNodesToJson(DocumentContext context, String key,
        Map<String, Map<String, Object>> nodeMaps) {
        if (Objects.isNull(context) || StringUtils.isBlank(key) || Objects.isNull(nodeMaps) || nodeMaps.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Map<String, Object>> md5Entry : nodeMaps.entrySet()) {
            for (Map.Entry<String, Object> entry : md5Entry.getValue().entrySet()) {
                context.put(JsonPath.compile("$..[?(@." + key + "=='" + md5Entry.getKey() + "')]"), entry.getKey(),
                    entry.getValue());
            }
        }
    }

    public static void putNodesToJson(DocumentContext context, Map<String, Map<String, Object>> nodeMaps) {
        putNodesToJson(context, DEFAULT_KEY, nodeMaps);
    }

    public static String putNodesToJson(String targetJson, Map<String, Map<String, Object>> nodeMaps) {
        DocumentContext context = JsonPath.using(JACKSON_CONFIGURATION).parse(targetJson);
        putNodesToJson(context, nodeMaps);
        return context.jsonString();
    }

    public static List<ScriptNode> getCurrentNodeByType(String nodeTree, String type) {
        return JsonPath.using(JACKSON_CONFIGURATION)
            .parse(nodeTree)
            .read("$..[?(@.type=='" + type + "')]", SCRIPT_NODE_TYPE_REF);
    }

    /**
     * 根据xpathMd5值获取其子节点
     *
     * @param nodeTree 目标json
     * @param xpathMd5 xpathMd5值
     * @param nodeType {@link NodeTypeEnum} 节点类型
     * @return List<ScriptNode>
     */
    public static List<ScriptNode> getChildrenByMd5(String nodeTree, String xpathMd5, NodeTypeEnum nodeType) {
        if (StringUtils.isBlank(nodeTree)) {
            return null;
        }
        if (StringUtils.isBlank(xpathMd5)) {
            if (null != nodeType) {
                return JsonPath.using(JACKSON_CONFIGURATION)
                    .parse(nodeTree)
                    .read("$..children[?(@.type=='" + nodeType.name() + "')]", SCRIPT_NODE_TYPE_REF);
            }
            return JsonPath.using(JACKSON_CONFIGURATION)
                .parse(nodeTree)
                .read("$..children", SCRIPT_NODE_TYPE_REF);
        }
        Object read = JsonPath.read(nodeTree, "$..[?(@.xpathMd5=='" + xpathMd5 + "')]");
        if (Objects.isNull(read)) {
            return null;
        }
        return JsonPath
            .using(JACKSON_CONFIGURATION)
            .parse(read.toString())
            .read("$..children[?(@.type=='" + nodeType + "')]", SCRIPT_NODE_TYPE_REF);
    }

    public static List<ScriptNode> getNodeListByType(String nodeTree, NodeTypeEnum nodeType) {
        return JsonPath
            .using(JACKSON_CONFIGURATION)
            .parse(nodeTree)
            .read("$..[?(@.type=='" + nodeType + "')]", SCRIPT_NODE_TYPE_REF);
    }

    /**
     * 根据xpathMd5值获取类型为控制器的子节点
     *
     * @param nodeTree 目标json
     * @param xpathMd5 xpathMd5值
     * @return List<ScriptNode>
     */
    public static List<ScriptNode> getChildControllers(String nodeTree, String xpathMd5) {
        return getChildrenByMd5(nodeTree, xpathMd5, NodeTypeEnum.CONTROLLER);
    }

    /**
     * 根据xpathMd5值获取类型为取样器的子节点
     *
     * @param nodeTree 目标json
     * @param xpathMd5 xpathMd5值
     * @return List<ScriptNode>
     */
    public static List<ScriptNode> getChildSamplers(String nodeTree, String xpathMd5) {
        return getChildrenByMd5(nodeTree, xpathMd5, NodeTypeEnum.SAMPLER);
    }
}
