package io.shulie.takin.cloud.common.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author qianshui
 * @date 2020/7/24 上午11:50
 */
@Slf4j
public class ParseXmlUtil {

    public static Map<String, String> parseHeaderXml(String xml) {
        Map<String, String> dataMap = Maps.newHashMap();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            boolean enabled = checkEnabled(rootElt);
            log.info("根节点：" + rootElt.getName() + "; 是否启用：" + enabled);
            if (!enabled) {
                return null;
            }
            List<Element> collectionProps = rootElt.elements("collectionProp");
            if (CollectionUtils.isEmpty(collectionProps)) {
                return null;
            }
            for (Element collectionProp : collectionProps) {
                if (!"HeaderManager.headers".equalsIgnoreCase(collectionProp.attributeValue("name"))) {
                    continue;
                }
                List<Element> elementProps = collectionProp.elements("elementProp");
                if (CollectionUtils.isEmpty(elementProps)) {
                    return null;
                }
                for (Element elementProp : elementProps) {
                    if (!"Header".equalsIgnoreCase(elementProp.attributeValue("elementType"))) {
                        continue;
                    }
                    List<Element> stringProps = elementProp.elements("stringProp");
                    if (CollectionUtils.isEmpty(stringProps)) {
                        continue;
                    }
                    String name = null;
                    String value = null;
                    for (Element stringProp : stringProps) {
                        if ("Header.name".equalsIgnoreCase(stringProp.attributeValue("name"))) {
                            name = StringUtils.replace(stringProp.getText(), " ", "");
                        } else if ("Header.value".equalsIgnoreCase(stringProp.attributeValue("name"))) {
                            value = StringUtils.replace(stringProp.getText().trim(), " ", "");
                        }
                    }
                    if (name != null && value != null) {
                        dataMap.put(name, value);
                    }
                }
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：Parse Jmeter Script Error --> 异常信息: {}",
                TakinCloudExceptionEnum.XML_PARSE_ERROR, e);
        }
        return dataMap;
    }

    private static boolean checkEnabled(Element element) {
        try {
            return "true".equalsIgnoreCase(element.attributeValue("enabled"));
        } catch (Exception e) {
            return false;
        }
    }
}
