package io.shulie.takin.cloud.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.constants.JmxConstant;
import io.shulie.takin.cloud.common.pojo.Pair;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 比JmxUtil解析的内容多一些，页面编辑调试用
 * @date 2021/10/13 12:02 下午
 */
@Slf4j
public class PtsJmxParseUtil {
    /**
     * 属性基本元素名称列表
     */
    private static final List<String> BASE_PROP_ELEMENTS = CollUtil.newArrayList("stringProp", "boolProp", "intProp", "doubleProp");

    /**
     * 从jmx文件中提取结构树,
     */
    public static List<ScriptNode> buildNodeTree(String file) {
        if (StrUtil.isBlank(file)) {
            return null;
        }
        File f = new File(file);
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        return buildNodeTree(f);
    }

    /**
     * 从jmx文件中提取结构树
     */
    public static List<ScriptNode> buildNodeTree(File file) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(file);
            Element root = document.getRootElement();
            if (null == root) {
                return null;
            }
            Element childContainer = root.element("hashTree");
            if (null == childContainer) {
                return null;
            }
            List<Element> elements = elements(childContainer);
            return buildNodeTree(elements, NodeTypeEnum.HASH_TREE);
        } catch (DocumentException e) {
            log.error("buildNodeTree DocumentException, file=" + file.getAbsolutePath(), e);
        }
        return null;
    }

    public static List<ScriptNode> buildNodeTree(List<Element> elements, NodeTypeEnum parentType) {
        if (CollUtil.isEmpty(elements)) {
            return null;
        }
        List<ScriptNode> nodes = CollUtil.newArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            ScriptNode node = buildNode(e, parentType);
            if (null == node) {
                continue;
            }
            if (i < elements.size() - 1) {
                Element nextElement = elements.get(i + 1);
                if ("hashTree".equals(nextElement.getName())) {
                    node.setChildren(buildNodeTree(elements(nextElement), node.getType()));
                }
            }
            nodes.add(node);
        }
        return nodes;
    }

    public static ScriptNode buildNode(Element element, NodeTypeEnum parentType) {
        String name = element.getName();
        NodeTypeEnum type = NodeTypeEnum.value(name);
        if (null == type) {
            return null;
        }
        if (isPtsNotEnabled(element, type)) {
            return null;
        }
        String testName = element.attributeValue("testname");
        ScriptNode node = new ScriptNode();
        node.setName(name);
        node.setTestName(testName);
        node.setType(type);
        node.setParentType(parentType);
        node.setXpath(element.getUniquePath());
        System.out.println(node.getXpath());
        node.setXpathMd5(Md5Util.md5(node.getXpath()));
        node.setMd5(Md5Util.md5(element.asXML()));
        node.setEnabled(Boolean.parseBoolean(element.attributeValue("enabled")));
        buildProps(node, element);
        return node;
    }

    /**
     * 返回禁用状态下线程组、取样器的数据
     * @param element
     * @param type
     * @return
     */
    public static boolean isPtsNotEnabled(Element element, NodeTypeEnum type) {
        if (null == element) {
            return true;
        }
        if(type == NodeTypeEnum.THREAD_GROUP || type == NodeTypeEnum.SAMPLER) {
            return false;
        }
        return !Boolean.parseBoolean(element.attributeValue("enabled"));
    }

    public static boolean isNotEnabled(Element element) {
        if (null == element) {
            return true;
        }
        return !Boolean.parseBoolean(element.attributeValue("enabled"));
    }

    public static void buildProps(ScriptNode node, Element element) {
        if (null == node || null == element) {
            return;
        }
        String name = node.getName();
        NodeTypeEnum type = node.getType();
        switch (type) {
            case THREAD_GROUP:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                if ("com.blazemeter.jmeter.threads.arrivals.FreeFormArrivalsThreadGroup".equals(name) || "kg.apc.jmeter.threads.UltimateThreadGroup".equals(name)) {
                    Element collectionProp = element.element("collectionProp");
                    JSONObject json = buildJSON(collectionProp);
                    if (null != json) {
                        String key = collectionProp.attributeValue("name");
                        Map<String, String> props = node.getProps();
                        props.put(key, json.toJSONString());
                    }
                }
                break;
            case SAMPLER:
                //如果是http取样器
                if ("HTTPSamplerProxy".equals(name) || "AjpSampler".equals(name)) {
                    Map<String, String> props = buildProps(element, BASE_PROP_ELEMENTS);
                    //找到http请求默认值
                    Element configElement = findConfigElement(element, "ConfigTestElement", "HttpDefaultsGui");
                    Map<String, String> configProps = buildProps(configElement, BASE_PROP_ELEMENTS);
                    props = mergeProps(props, configProps);
                    node.setProps(props);
                    //protocol+#+path+method, 即第一个#号前是protocol，最后一个#之后是method，中间的#可能是path自带
                    node.setSamplerType(SamplerTypeEnum.HTTP);
                    setHttpIdentification(node);

                } else if ("JavaSampler".equals(name)) {
                    Map<String, String> props = buildProps(element);
                    //找到java请求默认值
                    Element configElement = findConfigElement(element, "JavaConfig", "JavaConfigGui");
                    Map<String, String> configProps = buildProps(configElement);
                    props = mergeProps(props, configProps);
                    node.setProps(props);
                    //kafka:topic, 其他：返回null
                    setJavaSamplerIdentification(node);
                    node.setSamplerType(SamplerTypeEnum.JAVA);
                } else if ("FTPSampler".equals(name)) {
                    Map<String, String> props = buildProps(element);
                    Element configElement = findConfigElement(element, "ConfigTestElement", "FtpConfigGui");
                    Map<String, String> configProps = buildProps(configElement);
                    props = mergeProps(props, configProps);
                    node.setProps(props);
                    //server host
                    setIdentification(node, "FTPSampler.server", "FTPSampler.port");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("AccessLogSampler".equals(name)) {
                    node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                    //domain+#+port
                    setIdentification(node, "domain", "portString");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("LDAPSampler".equals(name)) {
                    Map<String, String> props = buildProps(element, BASE_PROP_ELEMENTS);
                    Element configElement = findConfigElement(element, "ConfigTestElement", "LdapConfigGui");
                    Map<String, String> configProps = buildProps(configElement, BASE_PROP_ELEMENTS);
                    props = mergeProps(props, configProps);
                    node.setProps(props);
                    //domain+#+port
                    setIdentification(node, "servername", "port");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("JDBCSampler".equals(name)) {
                    node.setProps(buildProps(element));
                    Element childrenContainerElement = findChildrenContainerElement(element);
                    if (null != childrenContainerElement) {
                        Element dataSource = childrenContainerElement.element("JDBCDataSource");
                        if (null != dataSource) {
                            Map<String, String> childProps = buildProps(dataSource);
                            if (null != childProps) {
                                Map<String, String> props = node.getProps();
                                if (null == props) {
                                    props = new HashMap<>(childProps.size());
                                }
                                props.putAll(childProps);
                                node.setProps(props);
                            }
                        }
                    }
                    //dbUrl
                    setIdentification(node, "dbUrl");
                    node.setSamplerType(SamplerTypeEnum.JDBC);
                } else if ("SmtpSampler".equals(name)) {
                    node.setProps(buildProps(element));
                    //domain+#+port
                    setIdentification(node, "SMTPSampler.server", "SMTPSampler.serverPort");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("TCPSampler".equals(name)) {
                    Map<String, String> props = buildProps(element);
                    Element configElement = findConfigElement(element, "ConfigTestElement", "TCPConfigGui");
                    Map<String, String> configProps = buildProps(configElement);
                    props = mergeProps(props, configProps);
                    node.setProps(props);
                    //domain+#+port
                    setIdentification(node, "TCPSampler.server", "TCPSampler.port");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("kg.apc.jmeter.samplers.DummySampler".equals(name)) {
                    node.setProps(buildProps(element));
                    //url
                    setIdentification(node, "URL");
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                } else if ("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample".equals(name)) {
                    node.setProps(buildProps(element));
                    //interface + # + method
                    setDubboIdentification(node);
                    node.setSamplerType(SamplerTypeEnum.DUBBO);
                }
                // TODO: 1.名称常量化。2.名称和解析逻辑的关系从主工程抽离。
                else if ("io.shulie.jmeter.plugins.rabbit.RabbitPublisherSampler".equals(name)) {
                    node.setProps(buildProps(element));
                    //interface + # + method
                    setRabbitIdentification(node);
                    node.setSamplerType(SamplerTypeEnum.RABBITMQ);
                } else if ("ShulieKafkaDataSetSampler".equals(name) || "io.shulie.jmeter.plugins.kafka.dataset.Sampler".equals(name)) {
                    node.setProps(buildProps(element));
                    Map<String, String> props = node.getProps();
                    if (null == props) {return;}
                    String prefix = props.get("prefix");
                    String suffix = props.get("suffix");
                    String text = StrUtil.format("{}Kafka数据集采样器{}", prefix, suffix);
                    node.setRequestPath(text);
                    node.setIdentification(text);
                    node.setSamplerType(SamplerTypeEnum.KAFKA);
                } else {
                    node.setProps(buildProps(element));
                    node.setSamplerType(SamplerTypeEnum.UNKNOWN);
                }
                break;
            case ARGUMENTS:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case HEADERMANAGER:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case POSTPROCESSOR:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case REGEXEXTRACTOR:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case ASSERTION:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case DATASET:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case CONFIG_TEST_ELEMENT:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case TIMER:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case COUNTER:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            case BEANSHELLPRE:
                node.setProps(buildProps(element, BASE_PROP_ELEMENTS));
                break;
            default:
                break;
        }
    }

    private static void setDubboIdentification(ScriptNode node) {
        if (null == node) {
            return;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return;
        }
        //解析dubbo接口和版本
        String dubboInterface = props.get("FIELD_DUBBO_INTERFACE");
        String fieldDubboVersion = props.get("FIELD_DUBBO_VERSION");
        String path = dubboInterface + ":" + fieldDubboVersion;

        //获取方法
        String fieldDubboMethod = props.get("FIELD_DUBBO_METHOD");

        //获取参数个数
        String fieldDubboMethodArgsSize = props.get("FIELD_DUBBO_METHOD_ARGS_SIZE");
        Integer argsSize = NumberUtil.parseInt(fieldDubboMethodArgsSize, 0);
        if (argsSize > 0) {
            StringBuilder method = new StringBuilder();
            method.append(fieldDubboMethod);
            method.append("(");
            //拼接参数
            for (int i = 1; i <= argsSize; i++) {
                String param = props.get("FIELD_DUBBO_METHOD_ARGS_PARAM_TYPE" + i);
                method.append(param).append(",");
            }
            //去掉最后一个逗号
            String substring = method.substring(0, method.length() - 1);
            String format = String.format("%s|%s|%s", substring + ")", path, SamplerTypeEnum.DUBBO.getRpcTypeEnum().getValue());
            node.setIdentification(format);
            node.setRequestPath(String.format("%s|%s", substring + ")", path));
        }
        String format = String.format("%s|%s|%s", fieldDubboMethod, path, SamplerTypeEnum.DUBBO.getRpcTypeEnum().getValue());
        node.setIdentification(format);
        node.setRequestPath(String.format("%s|%s", fieldDubboMethod, path));

    }

    private static void setRabbitIdentification(ScriptNode node) {
        if (null == node) {return;}
        Map<String, String> props = node.getProps();
        if (null == props) {return;}
        String exchange = props.get("RabbitSampler.Exchange");
        String routingKey = props.get("RabbitPublisher.MessageRoutingKey");
        if (StrUtil.isBlank(routingKey)) {
            routingKey = "*";
        }
        node.setRequestPath(String.format("%s|%s", routingKey, exchange));
        node.setIdentification(String.format("%s|%s|%s", routingKey, exchange, SamplerTypeEnum.RABBITMQ.getRpcTypeEnum().getValue()));
    }

    private static SamplerTypeEnum getJavaSamplerType(ScriptNode node) {
        if (null == node) {
            return SamplerTypeEnum.UNKNOWN;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return SamplerTypeEnum.UNKNOWN;
        }
        String javaClass = props.get("classname");
        if (StrUtil.isBlank(javaClass)) {
            return SamplerTypeEnum.UNKNOWN;
        }
        if ("co.signal.kafkameter.KafkaProducerSampler".equals(javaClass)) {
            return SamplerTypeEnum.KAFKA;
        }
        if ("com.gslab.pepper.sampler.PepperBoxKafkaSampler".equals(javaClass)) {
            return SamplerTypeEnum.KAFKA;
        }
        return SamplerTypeEnum.UNKNOWN;
    }

    public static JSONObject buildJSON(Element element) {
        if (null == element) {
            return null;
        }
        JSONObject json = new JSONObject();
        if (BASE_PROP_ELEMENTS.contains(element.getName())) {
            Pair<String, String> pair = getBasePropElementKeyAndValue(element);
            if (null != pair) {
                json.put(pair.getKey(), pair.getValue());
            }
        } else if ("collectionProp".equals(element.getName())) {
            String key = element.attributeValue("name");
            List<Element> elements = elements(element);
            if (CollUtil.isEmpty(elements)) {
                return json;
            }
            JSONArray arr = new JSONArray();
            json.put(key, arr);
            for (Element e : elements) {
                JSONObject v = buildJSON(e);
                arr.add(v);
            }
        } else if ("elementProp".equals(element.getName())) {
            String key = element.attributeValue("name");
            List<Element> elements = elements(element);
            if (CollUtil.isEmpty(elements)) {
                return json;
            }
            JSONObject value = new JSONObject();
            json.put(key, value);
            for (Element e : elements) {
                JSONObject v = buildJSON(e);
                value.putAll(v);
            }
        }
        return json;
    }

    /**
     * 查找默认请求元素
     *
     * @param element           当前元素
     * @param configElementName 默认请求元素名
     * @return 返回默认请求元素
     */
    public static Element findConfigElement(Element element, String configElementName, String guiClass) {
        List<?> elements = element.elements(configElementName);
        Element configElement = null;
        for (Object o : elements) {
            if (!(o instanceof Element)) {
                continue;
            }
            Element e = (Element)o;
            if (isNotEnabled(e)) {
                continue;
            }
            if (StrUtil.isNotBlank(guiClass) && !guiClass.equals(e.attributeValue("guiclass"))) {
                continue;
            }
            configElement = e;
            break;
        }
        if (null == configElement && !element.isRootElement()) {
            configElement = findConfigElement(element.getParent(), configElementName, guiClass);
        }
        return configElement;
    }

    /**
     * 寻找当前节点子节点，即当前节点的hashTree节点
     */
    public static Element findChildrenContainerElement(Element element) {
        if (null == element) {
            return null;
        }
        Element hashTree = null;
        if (element.isRootElement()) {
            hashTree = element.element("hashTree");
        } else {
            List<Element> elements = elements(element.getParent());
            for (int i = 0; i < elements.size(); i++) {
                Element e = elements.get(i);
                if (e == element && i < elements.size() - 1) {
                    Element next = elements.get(i + 1);
                    if ("hashTree".equals(next.getName())) {
                        hashTree = next;
                    }
                    break;
                }
            }
        }
        return hashTree;
    }

    /**
     * 合并多个props
     */
    public static Map<String, String> mergeProps(Map<String, String> p1, Map<String, String> p2) {
        if (null == p1) {
            return p2;
        }
        if (null == p2) {
            return p1;
        }

        for (Map.Entry<String, String> entry : p2.entrySet()) {
            String oldValue = p1.get(entry.getKey());
            if (StrUtil.isBlank(oldValue)) {
                p1.put(entry.getKey(), entry.getValue());
            }
        }
        return p1;
    }

    public static Map<String, String> buildProps(Element element, List<String> propElementNames) {
        return buildProps(element, propElementNames.toArray(new String[] {}));
    }

    /**
     * 提取prop数据
     *
     * @param element          prop的element对象
     * @param propElementNames 需要提取的element对象名称，为空表示不过滤
     * @return 返回prop数据
     */
    public static Map<String, String> buildProps(Element element, String... propElementNames) {
        if (null == element) {
            return null;
        }
        List<Element> elements = elements(element);
        if (CollUtil.isEmpty(elements)) {
            return null;
        }
        return elements.stream().filter(Objects::nonNull)
            .map(e -> PtsJmxParseUtil.getKeyAndValue(e, propElementNames))
            .filter(CollUtil::isNotEmpty)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .filter(p -> StrUtil.isNotBlank(p.getKey()) && Objects.nonNull(p.getValue()))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (o1, o2) -> o1));
    }

    public static Pair<String, String> getBasePropElementKeyAndValue(Element e) {
        String name = e.getName();
        if ("stringProp".equals(name) || "boolProp".equals(name) || "intProp".equals(name)) {
            return new Pair<>(e.attributeValue("name"), e.getText());
        } else if ("doubleProp".equals(name)) {
            Element nameElement = e.element("name");
            Element valeElement = e.element("value");

            String key = null;
            String value = "";
            if (null != nameElement) {
                key = nameElement.getText();
            }
            if (null != valeElement) {
                value = valeElement.getText();
            }
            if (StrUtil.isNotBlank(key)) {
                return new Pair<>(key, value);
            }
        }
        return null;
    }

    /**
     * 获取参数的键值对
     *
     * @param e                对象
     * @param propElementNames 允许获取的子元素列表
     * @return 返回键值对列表
     */
    public static List<Pair<String, String>> getKeyAndValue(Element e, String[] propElementNames) {
        if (null == e) {
            return null;
        }
        List<Pair<String, String>> result = CollUtil.newArrayList();
        String name = e.getName();
        if (BASE_PROP_ELEMENTS.contains(name)) {
            Pair<String, String> pair = getBasePropElementKeyAndValue(e);
            if (null != pair) {
                result.add(pair);
            }
        } else if ("elementProp".equals(name)) {
            List<Element> elements = elements(e);
            if (CollUtil.isEmpty(elements)) {
                return result;
            }
            for (Element element : elements) {
                List<Pair<String, String>> props = getKeyAndValue(element, propElementNames);
                result.addAll(props);
            }
            //聚合数据
            parseElementProp(result);
        } else if ("collectionProp".equals(e.getName())) {
            List<Element> elements = elements(e);
            if (CollUtil.isEmpty(elements)) {
                return result;
            }
            for (Element element : elements) {
                List<Pair<String, String>> props = getKeyAndValue(element, propElementNames);
                result.addAll(props);
            }
        }
        return result;
    }

    public static void setIdentification(ScriptNode node, String... keys) {
        if (null == node || null == keys || keys.length <= 0) {
            return;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return;
        }
        String collect = Arrays.stream(keys).filter(StrUtil::isNotBlank)
            .map(props::get)
            .collect(Collectors.joining("|"));
        node.setIdentification(collect);
        node.setRequestPath(collect);
    }

    public static void setJavaSamplerIdentification(ScriptNode node) {
        if (null == node) {
            return;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return;
        }
        String javaClass = props.get("classname");
        if (StrUtil.isBlank(javaClass)) {
            return;
        }
        if ("co.signal.kafkameter.KafkaProducerSampler".equals(javaClass)) {
            String topic = props.get("kafka_topic");
            if (StrUtil.isBlank(topic) || topic.startsWith("$")) {
                return;
            }
            node.setRequestPath(topic);
            node.setIdentification(String.format("%s|%s", topic, SamplerTypeEnum.KAFKA.getRpcTypeEnum().getValue()));
        } else if ("com.gslab.pepper.sampler.PepperBoxKafkaSampler".equals(javaClass)) {
            String topic = props.get("kafka.topic.name");
            if (StrUtil.isBlank(topic) || topic.startsWith("$")) {
                return;
            }
            node.setRequestPath(topic);
            node.setIdentification(String.format("%s|%s", topic, SamplerTypeEnum.KAFKA.getRpcTypeEnum().getValue()));
        } else {
            node.setRequestPath("POST|JavaRequest");
            node.setIdentification(node.getRequestPath() + "|0");
        }
    }

    public static void setHttpIdentification(ScriptNode node) {
        if (null == node) {
            return;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return;
        }
        String path = props.get("HTTPSampler.path");
        String method = props.get("HTTPSampler.method");
        int protocolCharIndex = null == path ? -1 : path.indexOf("://");
        if (protocolCharIndex != -1) {
            try {
                URL url = new URL(path);
                path = url.getPath();
            } catch (MalformedURLException e) {
                log.error("buildHttpIdentification MalformedURLException:path=" + path, e);
            }
        }
        path = pathGuiYi(path);
        String identification = String.format("%s|%s|%s", method, path, SamplerTypeEnum.HTTP.getRpcTypeEnum().getValue());
        node.setIdentification(identification);
        node.setRequestPath(String.format("%s|%s", method, path));
    }

    /**
     * http请求的path部分归一处理
     */
    public static String pathGuiYi(String path) {
        if (StrUtil.isBlank(path)) {
            return path;
        }
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.contains("#")) {
            String temp = path.substring(0, path.indexOf("#"));
            if (temp.endsWith("/")) {
                temp = temp.substring(0, temp.length() - 1);
                path = temp + path.substring(path.indexOf("#"));
            }
        } else {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    /**
     * 获取element下的所有子节点
     */
    public static List<Element> elements(Element element) {
        if (null == element) {
            return null;
        }
        List<?> elements = element.elements();
        if (CollUtil.isEmpty(elements)) {
            return null;
        }
        List<Element> list = CollUtil.newArrayList();
        for (Object o : elements) {
            if (o instanceof Element) {
                list.add((Element)o);
            }
        }
        return list;
    }

    private static void parseElementProp(List<Pair<String, String>> result) {
        if (CollUtil.isEmpty(result)) {
            return;
        }
        /**
         * 如果是
         *             <elementProp name="sso_port" elementType="Argument">
         *               <stringProp name="Argument.name">sso_port</stringProp>
         *               <stringProp name="Argument.value">8080</stringProp>
         *               <stringProp name="Argument.metadata">=</stringProp>
         *             </elementProp>
         * 类型的格式，则聚合后取1个；
         */
        String key = null;
        String value = null;
        for (Pair<String, String> p : result) {
            if ("Argument.name".equals(p.getKey())) {
                key = JmxConstant.argumentNamePrefix + p.getValue();
            } else if ("Argument.value".equals(p.getKey())) {
                value = p.getValue();
            } else if ("Argument.desc".equals(p.getKey())) {
                value = value + JmxConstant.valueDescSpilt + p.getValue();
            }
        }
        if (StrUtil.isNotBlank(key)) {
            result.clear();
            result.add(new Pair<>(key, value));
        } else {
            /**
             * 如果是
             *             `<elementProp name="" elementType="Header">
             *               <stringProp name="Header.name">Content-Type</stringProp>
             *               <stringProp name="Header.value">application/json</stringProp>
             *              </elementProp>`
             * 类型的格式，则聚合后取1个；
             */
            for (Pair<String, String> p : result) {
                if ("Header.name".equals(p.getKey())) {
                    key = p.getValue();
                } else if ("Header.value".equals(p.getKey())) {
                    value = p.getValue();
                }
            }
            if (StrUtil.isNotBlank(key)) {
                result.clear();
                result.add(new Pair<>(key, value));
            }
        }
    }
}
