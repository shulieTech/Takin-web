package io.shulie.takin.cloud.common.script.jmeter;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.cloud.common.script.util.SaxUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka 脚本解析器
 *
 * @author HengYu
 * @date 2021/4/12 4:02 下午
 */
public class XmlKafkaJmxParser extends JmxParser {

    private Logger log = LoggerFactory.getLogger(XmlKafkaJmxParser.class);

    @Override
    public List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt) {
        List<ScriptUrlExt> voList = new ArrayList<>();
        List<Element> allElement = SaxUtil.getAllElement("JavaSampler", document);
        for (Element element : allElement) {
            Attribute attribute = element.attribute("testname");
            String testName = attribute.getValue();
            if (testName.contains(" ")) {
                testName = testName.replaceAll(" ", "");
                attribute.setValue(testName);
            }
            String enable = element.attributeValue("enabled");
            Element kafkaClassName = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name", "classname",
                element.elements());
            if (kafkaClassName != null && "com.gslab.pepper.sampler.PepperBoxKafkaSampler".equals(
                kafkaClassName.getText())) {
                Element kafkaElement = SaxUtil.selectElementByEleNameAndAttr("elementProp", "name", "kafka.topic.name",
                    element.elements());
                if (kafkaElement != null) {
                    Element kafkaTopicElement = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name",
                        "Argument.value", kafkaElement.elements());
                    if (kafkaTopicElement != null) {
                        if (kafkaTopicElement.getText().startsWith("PT_")) {
                            scriptParseExt.setPtSize(scriptParseExt.getPtSize() + 1);
                        }
                        voList.add(new ScriptUrlExt(testName, "true".equals(enable), kafkaTopicElement.getText()));
                    }
                }
            }

            // add by lipeng 添加另一个kafka插件入口校验  20210607
            if (kafkaClassName != null && "co.signal.kafkameter.KafkaProducerSampler".equals(
                kafkaClassName.getText())) {
                Element kafkaElement = SaxUtil.selectElementByEleNameAndAttr("elementProp", "name", "kafka_topic",
                    element.elements());
                if (kafkaElement != null) {
                    Element kafkaTopicElement = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name",
                        "Argument.value", kafkaElement.elements());
                    if (kafkaTopicElement != null) {
                        if (kafkaTopicElement.getText().startsWith("PT_")) {
                            scriptParseExt.setPtSize(scriptParseExt.getPtSize() + 1);
                        }
                        voList.add(new ScriptUrlExt(testName, "true".equals(enable), kafkaTopicElement.getText()));
                    }
                }
            }
            // add end

        }
        return voList;
    }

}
