package io.shulie.takin.web.biz.service.pts;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.response.pts.PtsAssertResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsDebugRecordDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析jmx的查看结果数中的result.xml数据
 */
@Slf4j
public class PtsParseResultToObjectTools {

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(parseResultFile(new File("/Users/xiaoshu/Documents/jmx/result.xml"), 0)));
    }

    public static List<PtsDebugRecordDetailResponse> parseResultFile(File resultFile, Integer retryTimes) {
        List<PtsDebugRecordDetailResponse> detailResponses = new ArrayList<>();
        while (retryTimes <= 3) {
            try {
                detailResponses.addAll(buildNodeTree(resultFile));
                retryTimes = 4;
            } catch (Exception e) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {

                }
                retryTimes++;
            }
        }
        return detailResponses;
    }

    /**
     * 从jmx文件中提取结构树
     */
    private static List<PtsDebugRecordDetailResponse> buildNodeTree(File file) throws Exception {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(file);
            Element root = document.getRootElement();
            if (null == root) {
                return new ArrayList<>();
            }
            List<Element> elements = root.elements();
            return buildNodeTree(elements);
        } catch (Exception e) {
            log.error("PtsParseResultToObjectTools#buildNodeTree DocumentException, file=" + file.getAbsolutePath(), e);
            throw new Exception(e.getMessage());
        }
    }

    private static List<PtsDebugRecordDetailResponse> buildNodeTree(List<Element> elements) {
        if (CollUtil.isEmpty(elements)) {
            return new ArrayList<>();
        }
        List<PtsDebugRecordDetailResponse> nodes = CollUtil.newArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            PtsDebugRecordDetailResponse node = new PtsDebugRecordDetailResponse();
            node.setResponseStatus(Boolean.parseBoolean(element.attributeValue("s")));
            node.setRequestCost(element.attributeValue("t"));
            node.setRequestTime(DateUtil.formatDateTime(DateUtil.date(Long.parseLong(element.attributeValue("ts")))));
            node.getGeneral().setResponseCode(element.attributeValue("rc"));
            node.setApiName(element.attributeValue("lb"));
            node.getResponseData().setErrorMessage(element.attributeValue("rm"));
            buildNode(node, element);
            nodes.add(node);
        }
        return nodes;
    }

    private static void buildNode(PtsDebugRecordDetailResponse node, Element element) {
        List<Element> childElements = elements(element);
        for(Element childElement : childElements) {
            buildChildElement(node, childElement);
        }
    }

    private static void buildChildElement(PtsDebugRecordDetailResponse node, Element element) {
        String name = element.getName();
        switch (name) {
            case "assertionResult":
                List<Element> assertElements = elements(element);
                if(CollUtil.isNotEmpty(assertElements)) {
                    PtsAssertResponse response = new PtsAssertResponse();
                    for(Element e : assertElements) {
                        String eName = e.getName();
                        if(eName.equals("name")) {
                            response.setAssertName(e.getText());
                        } else if(eName.equals("failure")) {
                            response.setSuccess(!Boolean.parseBoolean(e.getText()));
                        } else if(eName.equals("failureMessage")) {
                            response.setFailureMessage(e.getText());
                        }
                    }
                    node.getResponseData().getAsserts().add(response);
                }
                break;
            case "responseHeader":
                node.getResponseData().setResponseHeaders(element.getText());
                break;
            case "requestHeader":
                node.getRequestData().setRequestHeaders(element.getText());
                break;
            case "responseData":
                node.getResponseData().setResponseBody(element.getText());
                break;
            case "method":
                node.getGeneral().setRequestMethod(element.getText());
                break;
            case "queryString":
                node.getRequestData().setRequestBody(element.getText());
                break;
            case "java.net.URL":
                node.getGeneral().setRequestUrl(element.getText());
                break;
            default:
                break;
        }
    }

    private static List<Element> elements(Element element) {
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
}
