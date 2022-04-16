package io.shulie.takin.cloud.common.script.jmeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.common.utils.ParseXmlUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import io.shulie.takin.constants.TakinRequestConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HengYu
 * @date 2021/4/12 4:02 下午
 */
public class XmlHttpJmxParser extends JmxParser {

    private Logger log = LoggerFactory.getLogger(XmlHttpJmxParser.class);

    public static final String HEADER_URL_NAME = "method";

    public static final String PT_NAME = TakinRequestConstant.CLUSTER_TEST_HEADER_KEY;

    public static final String PT_VALUE = TakinRequestConstant.CLUSTER_TEST_HEADER_VALUE;

    public static final String GET_CONCAT_CHARACTER = "?";

    @Override
    public List<ScriptUrlExt> getEntryContent(Document document, String content, ScriptParseExt scriptParseExt) {

        List<ScriptUrlExt> voList = new ArrayList<>();

        StringBuffer http = new StringBuffer().append("/").append("/").append("HTTPSamplerProxy");

        int httpSize = document.selectNodes(http.toString()).size();

        String namePath = new StringBuffer().append("/").append("/").append("HTTPSamplerProxy").append("/@").append(
            "testname").toString();

        String enablePath = new StringBuffer().append("/").append("/").append("HTTPSamplerProxy").append("/@")
            .append("enabled").toString();

        String urlPath = new StringBuffer().append("/").append("/").append("HTTPSamplerProxy")
            .append("/").append("/").append("stringProp")
            .append("[@").append("name=\"HTTPSampler.path\"]").toString();

        List list1;
        for (int index = 0; index < httpSize; index++) {
            Attribute attribute = (Attribute)document.selectNodes(namePath).get(index);
            String name = attribute.getValue();
            if (name.contains(" ")) {
                name = name.replaceAll(" ", "");
                attribute.setValue(name);
            }
            String enable = ((Attribute)document.selectNodes(enablePath).get(index)).getValue();
            list1 = ((Element)document.selectNodes(urlPath).get(index)).content();
            if (CollectionUtils.isEmpty(list1)) {
                continue;
            }
            String url = ((Text)list1.get(0)).getText();
            String headerXml = getHeaderXml(content, index);
            if (headerXml != null) {
                Map<String, String> headerMap = ParseXmlUtil.parseHeaderXml(headerXml);
                if (MapUtils.isNotEmpty(headerMap)) {
                    if (headerMap.containsKey(HEADER_URL_NAME)) {
                        url = headerMap.get(HEADER_URL_NAME);
                    }
                    if (headerMap.containsKey(PT_NAME) && PT_VALUE.equals(headerMap.get(PT_NAME))) {
                        scriptParseExt.setPtSize(scriptParseExt.getPtSize() + 1);
                    }
                }
            }
            if (url != null) {
                int pos = url.indexOf(GET_CONCAT_CHARACTER);
                if (pos > 0) {
                    url = url.substring(0, pos);
                }
            }
            voList.add(new ScriptUrlExt(name, "true".equals(enable), url));
        }
        if (CollectionUtils.isNotEmpty(voList)) {
            StringBuffer sb = new StringBuffer();
            voList.forEach(data -> sb.append(data.getName()).append(" ").append(data.getPath()).append("\n"));
            log.info("Parse Jmeter Script Result: " + sb);
        } else {
            log.info("Parse Jmeter Script Empty");
        }
        return voList;
    }

    /**
     * 获取第httpIndex对应的headerManager
     *
     * @param httpIndex -
     * @return -
     */
    private static String getHeaderXml(String xml, int httpIndex) {
        int times = 0;
        String tempXml = xml;
        int p1;
        while (times <= httpIndex) {
            p1 = tempXml.indexOf("<HTTPSamplerProxy");
            if (p1 == -1) {
                break;
            }
            tempXml = tempXml.substring(p1 + "<HTTPSamplerProxy".length());
            times++;
        }
        int p2 = tempXml.indexOf("<HTTPSamplerProxy");
        if (p2 > -1) {
            tempXml = tempXml.substring(0, p2);
        }
        int headerStart = tempXml.indexOf("<HeaderManager");
        int headerEnd = tempXml.indexOf("</HeaderManager>");
        if (headerStart == -1 || headerEnd == -1) {
            return null;
        }
        return tempXml.substring(headerStart, headerEnd + "</HeaderManager>".length());
    }

}
