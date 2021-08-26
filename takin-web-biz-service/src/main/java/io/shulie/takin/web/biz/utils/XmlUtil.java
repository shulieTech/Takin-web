package io.shulie.takin.web.biz.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

@Component
public class XmlUtil {

    public static Map<String, String> readStringXml(String xml) throws DocumentException {
        Document doc = DocumentHelper.parseText(xml);
        Element books = doc.getRootElement();
        Iterator elements = books.elementIterator();
        Map<String, String> map = new HashMap<>();
        while (elements.hasNext()) {
            Element user = (Element)elements.next();
            map.put(user.getName(), user.getText());
        }
        return map;
    }
}
