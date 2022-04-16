package io.shulie.takin.cloud.common.script.jmeter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.shulie.takin.cloud.common.script.util.SaxUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jdbc 插件 脚本解析器
 *
 * @author HengYu
 * @date 2021/4/12 4:02 下午
 */
public class XmlJdbcJmxParser extends JmxParser {

    public static final String JAVA_SAMPLER_ATTR_NAME = "testname";
    public static final String JAVA_SAMPLER = "JDBCSampler";
    private Logger log = LoggerFactory.getLogger(XmlJdbcJmxParser.class);

    @Override
    public List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt) {
        List<ScriptUrlExt> voList = new ArrayList<>();

        List<Element> allElement = SaxUtil.getAllElement(JAVA_SAMPLER, document);
        Set<Element> cache = new HashSet<>();
        for (Element element : allElement) {
            if (cache.contains(element)) {
                continue;
            }
            Element sampleParent = element.getParent();
            List<Element> elements = sampleParent.elements();
            Iterator<Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                processJdbcSample(voList, cache, iterator);
            }
        }
        log.info("jdbc parser jmx get entry size : {}", voList.size());
        return voList;
    }

    private void processJdbcSample(List<ScriptUrlExt> voList,
        Set<Element> cache, Iterator<Element> iterator) {
        Element subElement = iterator.next();

        if (subElement.getName().equals(JAVA_SAMPLER)) {

            ScriptUrlExt scriptUrlExt = new ScriptUrlExt();
            Attribute attribute = subElement.attribute(JAVA_SAMPLER_ATTR_NAME);
            String testName = attribute.getValue();
            if (testName.contains(" ")) {
                testName = testName.replaceAll(" ", "");
                attribute.setValue(testName);
            }

            String enable = subElement.attributeValue("enabled");
            scriptUrlExt.setEnable(Boolean.valueOf(enable));
            scriptUrlExt.setName(testName);

            Element nextElement = iterator.next();
            processHashTree(nextElement, scriptUrlExt);
            voList.add(scriptUrlExt);
        }
        cache.add(subElement);
    }

    private void processHashTree(Element hashTreeElement,
        ScriptUrlExt scriptUrlExt) {

        if (hashTreeElement != null) {
            Element headerPanelElement = SaxUtil.selectElementByEleNameAndAttr("HeaderManager", "testclass",
                "HeaderManager",
                hashTreeElement.elements());

            Element collectionPropElement = SaxUtil.selectElementByEleNameAndAttr("collectionProp", "name",
                "HeaderManager.headers", headerPanelElement.elements());

            List<Element> result = new ArrayList<>();
            SaxUtil.selectElement("stringProp", collectionPropElement.elements(), result);

            for (int i = 0; i < result.size(); i++) {
                Element element1 = result.get(i);
                String name = element1.attributeValue("name");
                String value = element1.getStringValue();
                if ("Header.name".equalsIgnoreCase(name) && "request_url".equalsIgnoreCase(value)) {
                    i++;
                    Element element2 = result.get(i);
                    if (element2 != null) {
                        String name2 = element2.attributeValue("name");
                        String value2 = element2.getText();
                        log.debug(name2 + "" + value2);
                        scriptUrlExt.setPath(value2);
                    }
                }
            }
        }
    }

}
