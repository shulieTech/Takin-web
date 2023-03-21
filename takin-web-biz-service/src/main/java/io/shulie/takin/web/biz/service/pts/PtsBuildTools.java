package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonAssert;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonProcessor;
import io.shulie.takin.cloud.common.script.jmeter.ScriptResponseAssert;
import io.shulie.takin.cloud.common.utils.PtsJmxBuildUtil;
import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import org.apache.commons.collections4.CollectionUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PtsBuildTools {

    public static String parseJmxString(String jsonString) throws MalformedURLException {
        PtsSceneRequest sceneRequest = JSON.parseObject(jsonString, PtsSceneRequest.class);
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        //创建基础脚本
        org.dom4j.Element rootHashEle = PtsJmxBuildUtil.buildBase(document);
        //创建测试计划
        org.dom4j.Element testPlan = PtsJmxBuildUtil.buildTestPlan(rootHashEle, sceneRequest.getProcessName());
        //数据源
        PtsDataSourceRequest dataSource = sceneRequest.getDataSource();
        if(dataSource != null) {
            PtsJmxBuildUtil.buildCsvData(testPlan, convertCsvData(dataSource.getCsvs()));
        }
        //创建线程组
        List<PtsLinkRequest> links = sceneRequest.getLinks();
        if(CollectionUtils.isNotEmpty(links)) {
            for(PtsLinkRequest link : links) {
                org.dom4j.Element threadGroupHashEle = PtsJmxBuildUtil.buildThreadGroup(testPlan, link.getLinkName());
                for(PtsApiRequest apiRequest : link.getApis()) {
                    //创建http请求
                    java.net.URL url = new java.net.URL(apiRequest.getBase().getRequestUrl());
                    org.dom4j.Element sampler = PtsJmxBuildUtil.buildHttpSampler(threadGroupHashEle, url, apiRequest.getBase().getRequestMethod(),
                            JSON.toJSONString(apiRequest.getBody().getForms()), apiRequest.getBody().getRawData());
                    //创建Http信息头
                    PtsJmxBuildUtil.buildHttpHeader(sampler, convertHeader(apiRequest.getHeader().getHeaders()));
                    //断言
                    PtsJmxBuildUtil.buildResponseAssert(sampler, convertResponseAssert(apiRequest.getCheckAssert()));
                    PtsJmxBuildUtil.buildJsonAssert(sampler, convertJsonAssert(apiRequest.getCheckAssert()));
                    //提取器
                    PtsJmxBuildUtil.buildJsonPostProcessor(sampler, convertProcessor(apiRequest.getReturnVar()));
                }
            }
        }
        return PtsJmxBuildUtil.getDocumentStr(document);
    }

    private static List<ScriptResponseAssert> convertResponseAssert(PtsApiAssertRequest request) {
        List<ScriptResponseAssert> asserts = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getAsserts())) {
            return asserts;
        }
        for(AssertRequest var : request.getAsserts()) {
            if(var.getCheckPointType().equals("响应body")) {
                ScriptResponseAssert scriptAssert = new ScriptResponseAssert();
                scriptAssert.setTestStrings(var.getCheckContent());
                scriptAssert.setTestField("Assertion.response_data");
                scriptAssert.setTestType(var.getCheckCondition());
                asserts.add(scriptAssert);
            }
        }
        return asserts;
    }

    private static List<ScriptJsonAssert> convertJsonAssert(PtsApiAssertRequest request) {
        List<ScriptJsonAssert> asserts = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getAsserts())) {
            return asserts;
        }
        for(AssertRequest var : request.getAsserts()) {
            if(var.getCheckPointType().equals("出参")) {
                ScriptJsonAssert jsonAssert = new ScriptJsonAssert();
                jsonAssert.setJsonPath(var.getCheckObject());
                jsonAssert.setJsonvalidation(Boolean.TRUE);
                jsonAssert.setExpectedValue(var.getCheckContent());
                asserts.add(jsonAssert);
            }
        }
        return asserts;
    }

    private static List<ScriptJsonProcessor> convertProcessor(PtsApiReturnVarRequest request) {
        List<ScriptJsonProcessor> processors = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getVars())) {
            return processors;
        }
        for(ReturnVarRequest var : request.getVars()) {
            ScriptJsonProcessor processor = new ScriptJsonProcessor();
            processor.setReferenceNames(var.getVarName());
            processor.setJsonPathExprs(var.getParseExpress());
            if(var.getMatchIndex() != null) {
                processor.setMatchNumbers(var.getMatchIndex().toString());
            }
            processors.add(processor) ;
        }
        return processors;
    }

    private static List<ScriptData> convertCsvData(List<PtsCsvRequest> csvRequests) {
        List<ScriptData> scriptList = new ArrayList<>();
        if(CollectionUtils.isEmpty(csvRequests)) {
            return scriptList;
        }
        for(PtsCsvRequest csvRequest : csvRequests) {
            ScriptData scriptData = new ScriptData();
            scriptData.setName(csvRequest.getFileName());
            scriptData.setPath(csvRequest.getFileName());
            scriptData.setFormat(convertCsvParams(csvRequest.getLineParams()));
            scriptData.setIgnoreFirstLine(csvRequest.getIngoreFirstLine());
            scriptList.add(scriptData);
        }
        return scriptList;
    }

    private static List<ScriptHeader> convertHeader(List<KeyValueRequest> headers) {
        List<ScriptHeader> scriptHeaders = new ArrayList<>();
        if(CollectionUtils.isEmpty(headers)) {
            return scriptHeaders;
        }
        for(KeyValueRequest header : headers) {
            ScriptHeader scriptHeader = new ScriptHeader();
            scriptHeader.setKey(header.getKey());
            scriptHeader.setValue(header.getValue());
            scriptHeaders.add(scriptHeader);
        }
        return scriptHeaders;
    }

    private static String convertCsvParams(List<PtsCsvLineRequest> lines) {
        StringBuffer sb = new StringBuffer();
        if(CollectionUtils.isEmpty(lines)) {
            return sb.toString();
        }
        Map<Integer, String> lineMap = new HashMap<>();
        int maxLine = 1;
        for(PtsCsvLineRequest line : lines) {
            lineMap.put(line.getLineNumber(), line.getParamsName());
            maxLine = Math.max(maxLine, line.getLineNumber());
        }
        for(int i = 1; i <= maxLine; i++) {
            if(lineMap.containsKey(i)) {
                sb.append(lineMap.get(i));
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }
}
