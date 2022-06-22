package io.shulie.takin.cloud.common.script.jmeter;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.cloud.common.script.util.SaxUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import io.shulie.takin.constants.TakinRequestConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dubbo 脚本解析器
 *
 * @author HengYu
 * @date 2021/4/12 4:02 下午
 */
public class XmlDubboJmxParser extends JmxParser {

    private Logger log = LoggerFactory.getLogger(XmlDubboJmxParser.class);

    public static final String HEADER_URL_NAME = "method";

    public static final String PT_NAME = TakinRequestConstant.CLUSTER_TEST_HEADER_KEY;

    public static final String PT_VALUE = TakinRequestConstant.CLUSTER_TEST_HEADER_VALUE;

    public static final String GET_CONCAT_CHARACTER = "?";

    @Override
    public List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt) {
        List<ScriptUrlExt> voList = new ArrayList<>();
        List<Element> allElement = SaxUtil.getAllElement("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample",
            document);

        for (Element element : allElement) {
            Attribute attribute = element.attribute("testname");
            String testname = attribute.getValue();
            if (testname.contains(" ")) {
                testname = testname.replaceAll(" ", "");
                attribute.setValue(testname);
            }
            String enable = element.attributeValue("enabled");
            Element fieldDubboInterface = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name",
                "FIELD_DUBBO_INTERFACE", element.elements());
            Element fieldDubboMethod = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name", "FIELD_DUBBO_METHOD",
                element.elements());
            if (fieldDubboInterface == null || fieldDubboMethod == null) {
                continue;
            }
            List<Element> stringPropList = new ArrayList<>();
            SaxUtil.selectElement("stringProp", element.elements(), stringPropList);

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
                    Element dubboAttachmentValue = SaxUtil.selectElementByEleNameAndAttr("stringProp", "name",
                        attachmentArgsValue, element.elements());
                    if (dubboAttachmentValue != null && "true".equals(dubboAttachmentValue.getText())) {
                        scriptParseExt.setPtSize(scriptParseExt.getPtSize() + 1);
                    }
                }
            }
            voList.add(new ScriptUrlExt(testname, "true".equals(enable),
                fieldDubboInterface.getText() + "#" + fieldDubboMethod.getText()));
        }
        return voList;
    }

}
