package io.shulie.takin.cloud.common.script.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.script.jmeter.XmlDubboJmxParser;
import io.shulie.takin.cloud.common.script.jmeter.XmlHttpJmxParser;
import io.shulie.takin.cloud.common.script.jmeter.XmlJdbcJmxParser;
import io.shulie.takin.cloud.common.script.jmeter.XmlKafkaJmxParser;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import io.shulie.takin.utils.file.FileManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * <ol>
 *     <li>获取jmx文件内容</li>
 *     <li>获取http接口数量</li>
 *     <li>循环获取http的name和url</li>
 *     <li>获取该http对应的header信息
 *     <p>
 * 通过字符串截取先获得整个<headerManager></headerManager>
 * 非启用状态的header不纳入计算范围
 * 再获取header里面的method(顺丰的请求url会放到这里），和User-Agent信息
 * </p>
 * </li>
 *     <li>替换url</li>
 * </ol>
 *
 * @author qianshui
 * @date 2020/4/22 上午4:09
 */
@Slf4j
public class SaxUtil {

    public static ScriptParseExt parseJmx(String path) {
        SAXReader saxReader = new SAXReader();
        ScriptParseExt scriptParseExt = new ScriptParseExt();
        try {
            File file = new File(path);
            String content = FileManagerHelper.readFileToString(file, "utf-8");
            // 读取文件内容
            Document document = saxReader.read(file);
            // 去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            List<ScriptUrlExt> scriptUrls = getScriptUrlFromJmx(scriptParseExt, content, document);
            scriptParseExt.setRequestUrl(scriptUrls);
        } catch (DocumentException e) {
            log.error("异常代码【{}】,异常内容：DocumentException", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：IOException", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
            throw new TakinCloudException(TakinCloudExceptionEnum.XML_PARSE_ERROR, "IOException", e);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：解析JMeter脚本错误。", TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
            throw new TakinCloudException(TakinCloudExceptionEnum.XML_PARSE_ERROR, "Exception", e);
        }
        return scriptParseExt;
    }

    private static List<ScriptUrlExt> getXmlJdbcContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlJdbcJmxParser parser = new XmlJdbcJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    private static List<ScriptUrlExt> getXmlKafkaContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlKafkaJmxParser parser = new XmlKafkaJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    /**
     * 解析并更新xml
     *
     * @param path 文件路径
     */
    public static void updateJmx(String path) {updateJmx(path, path);}

    /**
     * 解析并更新xml
     *
     * @param inputPath  输入文件路径
     * @param outputPath 输出文件路径
     */
    public static void updateJmx(String inputPath, String outputPath) {
        SAXReader saxReader = new SAXReader();
        try {
            File file = new File(inputPath);
            String content = FileManagerHelper.readFileToString(file, "utf-8");
            Document document = saxReader.read(new File(inputPath));
            //去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            getScriptUrlFromJmx(new ScriptParseExt(), content, document);
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, new OutputFormat());
            xmlWriter.write(document);
            xmlWriter.close();
            String xmlContent = writer.toString();
            if (inputPath.equals(outputPath)) {FileManagerHelper.deleteFilesByPath(inputPath);}
            FileManagerHelper.createFileByPathAndString(outputPath, xmlContent);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：Parse Jmeter Script Error --> 异常信息: {}",
                TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        }
    }

    private static List<ScriptUrlExt> getScriptUrlFromJmx(ScriptParseExt scriptParseExt, String content, Document document) {
        List<ScriptUrlExt> scriptUrls = new ArrayList<>();
        List<ScriptUrlExt> xmlHttpContent = getXmlHttpContent(document, content, scriptParseExt);
        List<ScriptUrlExt> dubboContents = getXmlDubboContent(document, content, scriptParseExt);
        List<ScriptUrlExt> kafkaContents = getXmlKafkaContent(document, content, scriptParseExt);
        List<ScriptUrlExt> jdbcContents = getXmlJdbcContent(document, content, scriptParseExt);

        if (CollectionUtils.isNotEmpty(xmlHttpContent)) {
            scriptUrls.addAll(xmlHttpContent);
        }
        if (CollectionUtils.isNotEmpty(dubboContents)) {
            scriptUrls.addAll(dubboContents);
        }
        if (CollectionUtils.isNotEmpty(kafkaContents)) {
            scriptUrls.addAll(kafkaContents);
        }
        if (CollectionUtils.isNotEmpty(jdbcContents)) {
            scriptUrls.addAll(jdbcContents);
        }
        log.info("jmx parser start ==================");
        scriptUrls.forEach((scriptUrlVO) -> log.info(JSON.toJSONString(scriptUrlVO)));
        log.info("jmx parser end ==================");
        return scriptUrls;
    }

    private static List<ScriptUrlExt> getXmlDubboContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlDubboJmxParser parser = new XmlDubboJmxParser();
        return parser.getEntryContent(document, content, scriptParseExt);
    }

    private static List<ScriptUrlExt> getXmlHttpContent(Document document, String content, ScriptParseExt scriptParseExt) {
        XmlHttpJmxParser httpJmxParser = new XmlHttpJmxParser();
        return httpJmxParser.getEntryContent(document, content, scriptParseExt);
    }

    public static List<Element> getAllElement(String elementName, Document document) {
        List<Element> result = new ArrayList<>();
        Element rootElement = document.getRootElement();
        selectElement(elementName, rootElement.elements(), result);
        return result;
    }

    public static void cleanAllDisableElement(Document document) {
        Element rootElement = document.getRootElement();
        cleanDisableElement(rootElement.elements());
    }

    /**
     * 重置重复的节点名称
     * <p>重名会添加#??????的后缀</p>
     * <p>只做控制器和采样器的去重</p>
     * <p>处理后不会出现空名称，会在空名称的基础上添加后缀</p>
     *
     * @param document xml文档
     */
    public static void resetRepetitiveName(Document document) {
        resetRepetitiveName(
            Collections.singletonList(document.getRootElement()),
            new HashSet<String>() {{add("");}});
    }

    /**
     * 重置重复的节点名称
     * <p>重名会添加#??????的后缀</p>
     * <p>只做控制器和采样器的去重</p>
     *
     * @param elements  节点列表 - 会遍历且递归{@link Element#elements()}
     * @param nodeNames 单次操作的名称容器
     */
    public static void resetRepetitiveName(List<?> elements, Set<String> nodeNames) {
        elements.stream().filter(t -> t instanceof Element).map(t -> (Element)t).forEach(t -> {
            // 节点是否是控制器或采样器
            String testClass = t.attributeValue("testclass");
            if (testClass == null || testClass.length() <= 0
                || (!testClass.contains("Sampler") && !testClass.contains("Controller"))) {} else {
                // 获取&&处理节点名称
                String nodeName = t.attributeValue("testname");
                nodeName = nodeName == null ? "" : nodeName;
                nodeName = nodeName.trim();
                // 声明变量以进行处理
                String newNodeName = nodeName;
                // 如果有重名情况的话，则给名称添加随机后缀
                if (!nodeNames.add(newNodeName)) {
                    // 防止随机数重复
                    Random random = new Random();
                    do {
                        newNodeName = nodeName + "#" + Double.valueOf((1 + random.nextDouble()) * Math.pow(10, 5)).intValue();
                    } while (!nodeNames.add(newNodeName));
                }
                if (!nodeName.equals(newNodeName)) {
                    t.attribute("testname").setText(newNodeName);
                }
            }
            resetRepetitiveName(t.elements(), nodeNames);
        });
    }

    public static void cleanDisableElement(List<?> elements) {
        if (CollectionUtils.isNotEmpty(elements)) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element)elements.get(i);
                cleanDisableElement(element.elements());
                if (element.attributeValue("enabled") != null && !"true".equals(element.attributeValue("enabled"))) {
                    if (elements.size() > i + 1) {
                        Element nextElement = (Element)elements.get(i + 1);
                        if ("hashTree".equals(nextElement.getName())) {
                            elements.remove(nextElement);
                        }
                    }
                    elements.remove(element);
                    i--;
                }
            }
        }
    }

    public static Element selectElementByEleNameAndAttr(String elementName, String attributeName, String attributeValue, List<?> elements) {
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        for (Object it : elements) {
            Element element = (Element)it;
            if (element.getName().equals(elementName) && attributeValue.equals(element.attributeValue(attributeName))) {
                return element;
            }
            Element childElement = selectElementByEleNameAndAttr(elementName, attributeName, attributeValue,
                element.elements());
            if (childElement != null) {
                return childElement;
            }
        }
        return null;
    }

    public static void selectElement(String elementName, List<?> elements, List<Element> result) {
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        for (Object o : elements) {
            Element element = (Element)o;
            if (element.getName().equals(elementName)) {
                result.add(element);
            }
            List<?> childElements = element.elements();
            selectElement(elementName, childElements, result);
        }
    }

    /**
     * 将dubbo压测标的值从true修改为false
     * 将http的压测标从PerfomanceTest 修改为flowDebug
     *
     * @param path 路径
     */
    public static void updatePressTestTags(String path) {
        SAXReader saxReader = new SAXReader();
        try {

            File file = new File(path);
            //因为新增场景脚本是异步的，这里最多等待5分钟
            int i = 0;
            while (!file.exists()) {
                i++;
                Thread.sleep(100L);
                if (i > 3000) {
                    return;
                }
            }

            // 读取文件内容
            Document document = saxReader.read(file);
            //去除所有禁用节点和对应的所有子节点
            cleanAllDisableElement(document);
            // 防止节点间名称重复
            resetRepetitiveName(document);
            updateJmxHttpPressTestTags(document);
            updateXmlDubboPressTestTags(document);

            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, new OutputFormat());
            xmlWriter.write(document);
            xmlWriter.close();
            String xmlContent = writer.toString();
            FileManagerHelper.deleteFilesByPath(path);
            FileManagerHelper.createFileByPathAndString(path, xmlContent);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：Parse Jmeter Script Error --> 异常信息: {}",
                TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        }
    }

    private static void updateXmlDubboPressTestTags(Document document) {

        List<Element> allElement = getAllElement("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample", document);
        for (Element element : allElement) {
            List<Element> stringPropList = new ArrayList<>();
            selectElement("stringProp", element.elements(), stringPropList);
            if (CollectionUtils.isNotEmpty(stringPropList)) {
                String attachmentArgsValue = "";
                for (Element ele : stringPropList) {
                    if (ele.attributeValue("name") != null && ele.attributeValue("name").startsWith(
                        "FIELD_DUBBO_ATTACHMENT_ARGS_KEY")
                        && "p-pradar-cluster-test".equals(ele.getText())) {
                        String attributeValue = ele.attributeValue("name");
                        attachmentArgsValue = attributeValue.replace("KEY", "VALUE");
                    }
                }
                if (StringUtils.isNotBlank(attachmentArgsValue)) {
                    Element dubboAttachmentValue = selectElementByEleNameAndAttr("stringProp", "name",
                        attachmentArgsValue, element.elements());
                    if (dubboAttachmentValue != null && "true".equals(dubboAttachmentValue.getText())) {
                        dubboAttachmentValue.setText("false");
                    }
                }
            }
        }
    }

    public static void updateJmxHttpPressTestTags(Document document) {
        List<Element> allElement = getAllElement("HeaderManager", document);
        if (CollectionUtils.isNotEmpty(allElement)) {
            List<Element> allElementProp = new ArrayList<>();
            for (Element headerElement : allElement) {
                selectElement("elementProp", headerElement.elements(), allElementProp);
            }
            if (CollectionUtils.isNotEmpty(allElementProp)) {
                for (Element elementProp : allElementProp) {
                    Element nameElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.name",
                        elementProp.elements());
                    Element valueElement = selectElementByEleNameAndAttr("stringProp", "name", "Header.value",
                        elementProp.elements());
                    if (nameElement != null && valueElement != null && "User-Agent".equals(nameElement.getText())
                        && "PerfomanceTest".equals(valueElement.getText())) {
                        valueElement.setText("FlowDebug");
                    }
                }
            }
        }
    }

}
