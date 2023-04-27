package io.shulie.takin.cloud.common.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.shulie.takin.cloud.common.enums.ThreadGroupTypeEnum;
import io.shulie.takin.cloud.common.pojo.Pair;
import io.shulie.takin.cloud.common.pojo.jmeter.ThreadGroupProperty;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author liyuanba
 * @date 2021/10/13 12:02 下午
 */
@Slf4j
public class JmxUtil {

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(buildNodeTree("/Users/xiaoshu/Desktop/JavaRequestTest2.jmx")));
    }
    /**
     * 属性基本元素名称列表
     */
    private static final List<String> BASE_PROP_ELEMENTS = CollUtil.newArrayList("stringProp", "boolProp", "intProp", "doubleProp");

    /**
     * 从jmx文件中提取结构树
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
            return buildNodeTree(elements);
        } catch (DocumentException e) {
            log.error("buildNodeTree DocumentException, file=" + file.getAbsolutePath(), e);
        }
        return null;
    }

    public static List<ScriptNode> buildNodeTree(List<Element> elements) {
        if (CollUtil.isEmpty(elements)) {
            return null;
        }
        List<ScriptNode> nodes = CollUtil.newArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            ScriptNode node = buildNode(e);
            if (null == node) {
                continue;
            }
            if (i < elements.size() - 1) {
                Element nextElement = elements.get(i + 1);
                if ("hashTree".equals(nextElement.getName())) {
                    node.setChildren(buildNodeTree(elements(nextElement)));
                }
            }
            nodes.add(node);
        }
        return nodes;
    }

    public static ScriptNode buildNode(Element element) {
        if (isNotEnabled(element)) {
            return null;
        }
        String name = element.getName();
        NodeTypeEnum type = NodeTypeEnum.value(name);
        if (null == type) {
            return null;
        }
        String testName = element.attributeValue("testname");
        ScriptNode node = new ScriptNode();
        node.setName(name);
        node.setTestName(testName);
        node.setType(type);
        node.setXpath(element.getUniquePath());
        node.setXpathMd5(Md5Util.md5(node.getXpath()));
        node.setMd5(Md5Util.md5(element.asXML()));
        buildProps(node, element);
        return node;
    }

    public static boolean isNotEnabled(Element element) {
        if (null == element) {
            return true;
        }
        return !Boolean.parseBoolean(element.attributeValue("enabled"));
    }

    /**
     * 从脚本中提取最大线程数、压测时长、线程增长配置等信息
     */
    public static ThreadGroupProperty getFromThreadGroupNode(ScriptNode node) {
        if (null == node || NodeTypeEnum.THREAD_GROUP != node.getType()) {
            return null;
        }
        Map<String, String> props = node.getProps();
        if (null == props) {
            return null;
        }
        ThreadGroupProperty p = new ThreadGroupProperty();
        if ("ThreadGroup".equals(node.getName())) {
            //普通线程组
            p.setType(ThreadGroupTypeEnum.CONCURRENCY);
            p.setMaxThreadNum(NumberUtil.parseInt(props.get("ThreadGroup.num_threads")));
            p.setDuration(NumberUtil.parseInt(props.get("ThreadGroup.duration")));
            p.setRampUp(NumberUtil.parseInt(props.get("ThreadGroup.ramp_time")));
        } else if ("com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup".equals(node.getName())) {
            p.setType(ThreadGroupTypeEnum.TPS);
            String unit = props.get("Unit");
            int unitRadix = 1;
            if ("M".equals(unit)) {
                unitRadix = 60;
            }
            p.setTps(NumberUtil.parseInt(props.get("TargetLevel")) * unitRadix);
            p.setRampUp(NumberUtil.parseInt(props.get("RampUp")) * unitRadix);
            p.setSteps(NumberUtil.parseInt(props.get("Steps")));
            p.setDuration(NumberUtil.parseInt(props.get("Hold")) * unitRadix + p.getRampUp());
            p.setMaxThreadNum(NumberUtil.parseInt(props.get("ConcurrencyLimit")));
        } else if ("com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup".equals(node.getName())) {
            p.setType(ThreadGroupTypeEnum.CONCURRENCY);
            String unit = props.get("Unit");
            int unitRadix = 1;
            if ("M".equals(unit)) {
                unitRadix = 60;
            }
            p.setRampUp(NumberUtil.parseInt(props.get("RampUp")) * unitRadix);
            p.setSteps(NumberUtil.parseInt(props.get("Steps")));
            p.setDuration(NumberUtil.parseInt(props.get("Hold")) * unitRadix + p.getRampUp());
            p.setMaxThreadNum(NumberUtil.parseInt(props.get("TargetLevel")));
        } else if ("com.blazemeter.jmeter.threads.arrivals.FreeFormArrivalsThreadGroup".equals(node.getName())) {
            p.setType(ThreadGroupTypeEnum.CONCURRENCY);
            String unit = props.get("Unit");
            int unitRadix = 1;
            if ("M".equals(unit)) {
                unitRadix = 60;
            }
            p.setMaxThreadNum(NumberUtil.parseInt(props.get("ConcurrencyLimit")));
            JSONObject json = JsonUtil.parse(props.get("Schedule"));
            if (null == json) {
                return p;
            }
            JSONArray jsonArray = json.getJSONArray("Schedule");
            if (null == jsonArray) {
                return p;
            }
            int duration = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                if (null == o) {
                    continue;
                }
                for (String k : o.keySet()) {
                    JSONArray arr = o.getJSONArray(k);
                    if (null == arr) {
                        continue;
                    }
                    int time = getValueFromJsonObject(arr.getJSONObject(2));
                    duration += time;
                }
            }
            p.setDuration(duration * unitRadix);
        } else if ("kg.apc.jmeter.threads.UltimateThreadGroup".equals(node.getName())) {
            JSONObject json = JsonUtil.parse(props.get("ultimatethreadgroupdata"));
            if (null == json) {
                return p;
            }
            JSONArray jsonArray = json.getJSONArray("ultimatethreadgroupdata");
            int duration = 0;
            List<Map<String, Integer>> shcdules = CollUtil.newArrayList();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                if (null == o || CollUtil.isEmpty(o.values())) {
                    continue;
                }
                JSONArray arr = o.values().stream().filter(Objects::nonNull)
                    .map(v -> (JSONArray)v)
                    .findFirst()
                    .orElse(null);
                if (null == arr) {
                    continue;
                }
                int threadNum = getValueFromJsonObject(arr.getJSONObject(0));
                int initialDelay = getValueFromJsonObject(arr.getJSONObject(1));
                int startUp = getValueFromJsonObject(arr.getJSONObject(2));
                int hold = getValueFromJsonObject(arr.getJSONObject(3));
                int shutDown = getValueFromJsonObject(arr.getJSONObject(4));
                Map<String, Integer> m = new HashMap<>(5);
                m.put("threadNum", threadNum);
                m.put("initialDelay", initialDelay);
                m.put("startUp", startUp);
                m.put("hold", hold);
                m.put("shutDown", shutDown);
                shcdules.add(m);
                int t = initialDelay + startUp + hold + shutDown;
                if (duration < t) {
                    duration = t;
                }
            }
            p.setDuration(duration);
            double maxThreadNum = 0;
            for (int i = 0; i <= duration; i++) {
                double nowThreadNum = 0d;
                for (Map<String, Integer> m : shcdules) {
                    int threadNum = m.get("threadNum");
                    int initialDelay = m.get("initialDelay");
                    int hold = m.get("hold");
                    int startUp = m.get("startUp");
                    int shutDown = m.get("shutDown");
                    double step = threadNum;
                    if (startUp > 0) {
                        step /= startUp;
                    }
                    int startUpTime = initialDelay + startUp;
                    int holdTime = startUpTime + hold;
                    int shutDownTime = holdTime + shutDown;
                    if (i <= initialDelay) {
                        nowThreadNum += 0d;
                    } else if (i < startUpTime) {
                        nowThreadNum += step * (i - initialDelay);
                    } else if (i <= holdTime) {
                        nowThreadNum += threadNum;
                    } else if (i < shutDownTime) {
                        nowThreadNum += threadNum - (((double)threadNum / shutDown) * (i - holdTime));
                    } else if (i > shutDownTime) {
                        nowThreadNum += 0;
                    }
                }
                if (maxThreadNum < nowThreadNum) {
                    maxThreadNum = nowThreadNum;
                }
            }
            p.setMaxThreadNum((int)Math.ceil(maxThreadNum));
        }
        return p;
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
                    node.setSamplerType(getJavaSamplerType(node));
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
        return SamplerTypeEnum.JAVA;
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
            .map(e -> JmxUtil.getKeyAndValue(e, propElementNames))
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
        if (null != propElementNames && propElementNames.length > 0) {
            boolean contains = Arrays.stream(propElementNames).filter(Objects::nonNull)
                .anyMatch(s -> s.equals(name));
            if (!contains) {
                return null;
            }
        }
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
            String key = null;
            String value = "";
            for (Element element : elements) {
                if ("collectionProp".equals(element.getName())) {
                    List<Pair<String, String>> props = getKeyAndValue(element, propElementNames);
                    result.addAll(props);
                } else {
                    List<Pair<String, String>> pairs = getKeyAndValue(element, propElementNames);
                    if (CollUtil.isNotEmpty(pairs)) {
                        for (Pair<String, String> p : pairs) {
                            if (StrUtil.isBlank(p.getValue())) {
                                continue;
                            }
                            if ("Argument.name".equals(p.getKey())) {
                                key = p.getValue();
                            } else if ("Argument.value".equals(p.getKey())) {
                                value = p.getValue();
                            }
                        }
                    }
                }
            }
            if (StrUtil.isNotBlank(key)) {
                result.add(new Pair<>(key, value));
            }
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

    public static int getValueFromJsonObject(JSONObject json) {
        if (null == json || CollUtil.isEmpty(json.values())) {
            return 0;
        }
        return json.values().stream().filter(Objects::nonNull)
            .map(o -> (String)o)
            .filter(StrUtil::isNotBlank)
            .map(NumberUtil::parseInt)
            .findFirst()
            .orElse(0);
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

    /**
     * 将层次结构的节点变成平铺式的集合
     *
     * @param nodes 有层次节点列表
     * @return 平铺式节点列表
     */
    public static List<ScriptNode> toOneDepthList(List<ScriptNode> nodes) {
        if (CollUtil.isEmpty(nodes)) {
            return null;
        }
        List<ScriptNode> list = new ArrayList<>();
        for (ScriptNode node : nodes) {
            list.add(node);
            List<ScriptNode> children = toOneDepthList(node.getChildren());
            if (children != null && children.size() > 0) {
                list.addAll(children);
            }
        }
        return list;
    }

    /**
     * 根据节点类型获取节点数量
     */
    public static int getNodeNumByType(NodeTypeEnum typeEnum, List<ScriptNode> data) {
        int nodeNum = 0;
        if (CollUtil.isNotEmpty(data)) {
            for (ScriptNode scriptNode : data) {
                if (scriptNode.getType() == typeEnum) {
                    nodeNum++;
                }
                if (CollUtil.isNotEmpty(scriptNode.getChildren())) {
                    nodeNum = nodeNum + getNodeNumByType(typeEnum, scriptNode.getChildren());
                }
            }
        }
        return nodeNum;
    }

    /**
     * 获取对应类型的节点展开列表
     *
     * @param typeEnum 类型枚举
     * @param data     脚本解析数据
     * @return 结果
     */
    public static List<ScriptNode> getScriptNodeByType(NodeTypeEnum typeEnum, List<ScriptNode> data) {
        List<ScriptNode> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(data)) {
            for (ScriptNode scriptNode : data) {
                if (scriptNode.getType() == typeEnum) {
                    result.add(scriptNode);
                }
                if (CollUtil.isNotEmpty(scriptNode.getChildren())) {
                    List<ScriptNode> scriptNodeByType = getScriptNodeByType(typeEnum, scriptNode.getChildren());
                    if (CollUtil.isNotEmpty(scriptNodeByType)) {
                        result.addAll(scriptNodeByType);
                    }
                } else {
                    scriptNode.setChildren(null);
                }
            }
        }
        return result;
    }

    public static List<ScriptNode> getChildNodesByFilterFunc(ScriptNode node, Function<ScriptNode, Boolean> filterFunc) {
        if (null == node || CollUtil.isEmpty(node.getChildren())) {
            return null;
        }
        List<ScriptNode> result = CollUtil.newArrayList();
        for (ScriptNode childNode : node.getChildren()) {
            result.add(childNode);
            if (filterFunc.apply(childNode)) {
                List<ScriptNode> subNodes = getChildNodesByFilterFunc(childNode, filterFunc);
                if (CollUtil.isNotEmpty(subNodes)) {
                    result.addAll(subNodes);
                }
            }
        }
        return result;
    }
}
