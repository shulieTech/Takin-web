package io.shulie.takin.web.biz.service.pts;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.enums.pts.PtsAssertConditionEnum;
import io.shulie.takin.cloud.common.enums.pts.PtsAssertTypeEnum;
import io.shulie.takin.cloud.common.enums.pts.PtsVarSourceEnum;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonAssert;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonProcessor;
import io.shulie.takin.cloud.common.script.jmeter.ScriptResponseAssert;
import io.shulie.takin.cloud.common.utils.PtsJmxBuildUtil;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PtsBuildJsonToJmxTextTools {

    public static void main(String[] args) throws Exception{
        System.out.println(parseJmxString("{\"id\":\"997\",\"processName\":\"jmx-test-csv_2023-03-3019:28:04\",\"links\":[{\"linkName\":\"线程组\",\"apis\":[{\"apiName\":\"userRegister-error\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"requestMethod\":\"POST\",\"requestTimeout\":null,\"requestUrl\":\"http://192.168.1.222:28881/gateway/api/register\"},\"body\":{\"rawData\":\"{\\n    \\\\\\\"mobile\\\\\\\":\\\\\\\"15558172201\\\\\\\",\\n    \\\\\\\"password\\\\\\\":\\\\\\\"${password}\\\\\\\",\\n    \\\\\\\"nickName\\\\\\\":\\\\\\\"${userName}_${takin-domain}\\\\\\\",\\n    \\\\\\\"email\\\\\\\":\\\\\\\"10000000@qq.com\\\\\\\",\\n    \\\\\\\"birthDay\\\\\\\":\\\\\\\"2000-11-${id}\\\\\\\",\\n    \\\\\\\"provinceName\\\\\\\":\\\\\\\"浙江\\\\\\\",\\n    \\\\\\\"cityName\\\\\\\":\\\\\\\"杭州\\\\\\\"\\n }\"},\"checkAssert\":{\"asserts\":[]},\"header\":{\"headers\":[{\"key\":\"Content-Type\",\"value\":\"application/json\"}]},\"returnVar\":{\"vars\":[]}},{\"apiName\":\"userRegister-normal\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"requestMethod\":\"POST\",\"requestTimeout\":null,\"requestUrl\":\"http://192.168.1.222:28881/gateway/api/register\"},\"body\":{\"rawData\":\"{\\n    \\\\\\\"mobile\\\\\\\":\\\\\\\"15558172201\\\\\\\",\\n    \\\\\\\"password\\\\\\\":\\\\\\\"123456\\\\\\\",\\n    \\\\\\\"nickName\\\\\\\":\\\\\\\"${takin-domain}\\\\\\\",\\n    \\\\\\\"email\\\\\\\":\\\\\\\"10000000@qq.com\\\\\\\",\\n    \\\\\\\"birthDay\\\\\\\":\\\\\\\"2000-11-05\\\\\\\",\\n    \\\\\\\"provinceName\\\\\\\":\\\\\\\"浙江\\\\\\\",\\n    \\\\\\\"cityName\\\\\\\":\\\\\\\"杭州\\\\\\\"\\n }\"},\"checkAssert\":{\"asserts\":[]},\"header\":{\"headers\":[{\"key\":\"Content-Type\",\"value\":\"application/json\"}]},\"returnVar\":{\"vars\":[{\"testName\":\"正则表达式提取器\",\"varName\":\"code\",\"varSource\":\"Body:JSON\",\"parseExpress\":\"”code\\\\\\\":(.*)\",\"matchIndex\":1}]}},{\"apiName\":\"串联链路\",\"apiType\":\"JAVA\",\"base\":{\"requestUrl\":\"com.hzbank.RequestIB2\"},\"param\":{\"params\":[{\"paramCNDesc\":\"请求名称\",\"paramName\":\"requestLabel\",\"paramValue\":\"IB2请求\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false},{\"paramCNDesc\":\"IBP地址\",\"paramName\":\"ibmsAddress\",\"paramValue\":\"66.88.1.67\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false},{\"paramCNDesc\":\"IBP端口\",\"paramName\":\"ibmsPorg\",\"paramValue\":\"16001\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false},{\"paramCNDesc\":\"服务端节点\",\"paramName\":\"recvNode\",\"paramValue\":\"0000\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false},{\"paramCNDesc\":\"交易码\",\"paramName\":\"appCode\",\"paramValue\":\"TESTECHO\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false},{\"paramCNDesc\":\"报文内容\",\"paramName\":\"content\",\"paramValue\":\"\",\"paramType\":\"text\",\"require\":false,\"allowEdit\":false},{\"paramCNDesc\":\"本机IP\",\"paramName\":\"localhost\",\"paramValue\":\"选填(双网卡机器需填写)\",\"paramType\":\"text\",\"require\":false,\"allowEdit\":false},{\"paramCNDesc\":\"文件1\",\"paramName\":\"file1\",\"paramValue\":\"/data/nfs_dir/scriptfile/temp/f6a1c97e-47fd-429c-95e0-e6595aa7c7a0/[object Object].txt\",\"paramType\":\"file\",\"require\":false,\"allowEdit\":false},{\"paramCNDesc\":\"文件2\",\"paramName\":\"file2\",\"paramValue\":\"/data/nfs_dir/scriptfile/temp/19b53726-8e71-4136-a3a6-22ae655f197b/[object Object].txt\",\"paramType\":\"file\",\"require\":false,\"allowEdit\":false}]}}]}],\"dataSource\":{\"csvs\":[{\"fileName\":\"id.csv\",\"params\":\"id\",\"ingoreFirstLine\":true},{\"fileName\":\"user.csv\",\"params\":\"userName,password\",\"ingoreFirstLine\":true}]}}\n"));
    }

    public static String parseJmxString(String jsonString) throws Exception {
        PtsSceneRequest sceneRequest = JSON.parseObject(jsonString, PtsSceneRequest.class);
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        //创建基础脚本
        org.dom4j.Element rootHashEle = PtsJmxBuildUtil.buildBase(document);
        //创建测试计划
        org.dom4j.Element testPlan = PtsJmxBuildUtil.buildTestPlan(rootHashEle, sceneRequest.getProcessName());
        //数据源
        PtsDataSourceRequest dataSource = sceneRequest.getDataSource();
        PtsJmxBuildUtil.buildCsvData(testPlan, convertCsvData(dataSource.getCsvs()));
        //全局HTTP请求头
        PtsApiHeaderRequest globalHeader = sceneRequest.getGlobalHeader();
        PtsJmxBuildUtil.buildHttpHeader(testPlan, convertHeader(globalHeader.getHeaders()));
        //全局HTTP请求
        PtsGlobalHttpRequest globalHttp = sceneRequest.getGlobalHttp();
        PtsJmxBuildUtil.buildHttpDefault(testPlan, JSONObject.parseObject(JSON.toJSONString(globalHttp)));
        //计数器
        List<PtsCounterRequest> counters = sceneRequest.getCounters();
        if(CollectionUtils.isNotEmpty(counters)) {
            List<JSONObject> counterJsonList = new ArrayList<>();
            for (PtsCounterRequest counter : counters) {
                counterJsonList.add(JSONObject.parseObject(JSON.toJSONString(counter)));
            }
            PtsJmxBuildUtil.buildCounter(testPlan, counterJsonList);
        }
        //创建线程组
        List<PtsLinkRequest> links = sceneRequest.getLinks();
        if(CollectionUtils.isNotEmpty(links)) {
            for(PtsLinkRequest link : links) {
                org.dom4j.Element threadGroupHashEle = PtsJmxBuildUtil.buildThreadGroup(testPlan, link.getLinkName());
                for(PtsApiRequest apiRequest : link.getApis()) {
                    if(StringUtils.isBlank(apiRequest.getApiType()) || SamplerTypeEnum.HTTP.getType().equals(apiRequest.getApiType())) {
                        if(StringUtils.isBlank(apiRequest.getBase().getRequestMethod()) || StringUtils.isBlank(apiRequest.getBase().getRequestUrl())) {
                            continue;
                        }
                        //创建http请求
                        java.net.URL url = new java.net.URL(apiRequest.getBase().getRequestUrl());
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("requestTimeout", apiRequest.getBase().getRequestTimeout() != null ? String.valueOf(apiRequest.getBase().getRequestTimeout()) : "");
                        paramMap.put("allowForward", apiRequest.getBase().getAllowForward().toString());
                        paramMap.put("keepAlive", apiRequest.getBase().getKeepAlive().toString());
                        org.dom4j.Element sampler = PtsJmxBuildUtil.buildHttpSampler(threadGroupHashEle, apiRequest.getApiName(),
                                url, apiRequest.getBase().getRequestMethod(),
                                JSON.toJSONString(apiRequest.getBody().getForms()),
                                apiRequest.getBody().getRawData(),
                                paramMap);
                        //POST请求，自动加Content-Type
                        if(apiRequest.getBase().getRequestMethod().equals("POST")) {
                            apiRequest.getHeader().getHeaders().add(new KeyValueRequest("Content-Type", "application/json"));
                        }
                        //创建定时器
                        PtsJmxBuildUtil.buildTimer(sampler, apiRequest.getTimer().getDelay());
                        //创建Http信息头
                        PtsJmxBuildUtil.buildHttpHeader(sampler, convertHeader(apiRequest.getHeader().getHeaders()));
                        //BeanShellPre
                        PtsJmxBuildUtil.buildApiBeanshellPre(sampler, apiRequest.getBeanShellPre().getScript());
                        //断言
                        PtsJmxBuildUtil.buildResponseAssert(sampler, convertResponseAssert(apiRequest.getCheckAssert()));
                        PtsJmxBuildUtil.buildJsonAssert(sampler, convertJsonAssert(apiRequest.getCheckAssert()));
                        //提取器
                        PtsJmxBuildUtil.buildJsonPostProcessor(sampler, convertJsonProcessor(apiRequest.getReturnVar()));
                        PtsJmxBuildUtil.buildRegexExtractor(sampler, convertRegexExtractor(apiRequest.getReturnVar()));
                    } else if(SamplerTypeEnum.JAVA.getType().equals(apiRequest.getApiType())) {
                        //创建Java请求
                        if(StringUtils.isBlank(apiRequest.getBase().getRequestUrl())) {
                            continue;
                        }
                        PtsJmxBuildUtil.buildJavaSampler(threadGroupHashEle, apiRequest.getApiName(), apiRequest.getBase().getRequestUrl(), convertJavaParam2Map(apiRequest.getParam().getParams()));
                    }
                }
            }
        }
        //创建查看结果树
        PtsJmxBuildUtil.buildViewResultsTree(testPlan);
        return XmlUtil.format(PtsJmxBuildUtil.getDocumentStr(document));
    }

    private static Map<String, String> convertJavaParam2Map(List<JavaParamRequest> requestList) {
        Map<String, String> dataMap = new HashMap<>();
        if(CollectionUtils.isEmpty(requestList)) {
            return dataMap;
        }
        requestList.forEach(data -> dataMap.put(data.getParamName(), data.getParamValue()));
        return dataMap;
    }

    private static Map<String, String> convertKeyValue2Map(List<KeyValueRequest> requestList) {
        Map<String, String> dataMap = new HashMap<>();
        if(CollectionUtils.isEmpty(requestList)) {
            return dataMap;
        }
        requestList.forEach(data -> dataMap.put(data.getKey(), data.getValue()));
        return dataMap;
    }

    private static List<ScriptResponseAssert> convertResponseAssert(PtsApiAssertRequest request) {
        List<ScriptResponseAssert> asserts = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getAsserts())) {
            return asserts;
        }
        for(AssertRequest var : request.getAsserts()) {
            PtsAssertTypeEnum typeEnum = PtsAssertTypeEnum.getByName(var.getCheckPointType());
            if(typeEnum == PtsAssertTypeEnum.RESPONSE_BODY || typeEnum == PtsAssertTypeEnum.RESPONSE_CODE) {
                PtsAssertConditionEnum conditionEnum = PtsAssertConditionEnum.getByName(var.getCheckCondition());
                ScriptResponseAssert scriptAssert = new ScriptResponseAssert();
                scriptAssert.setTestStrings(var.getCheckContent());
                scriptAssert.setTestField(typeEnum.getValue());
                scriptAssert.setTestType(conditionEnum.getValue());
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
            if(var.getCheckPointType().equals(PtsAssertTypeEnum.PARAMS.getName())) {
                ScriptJsonAssert jsonAssert = new ScriptJsonAssert();
                jsonAssert.setJsonPath(var.getCheckObject());
                jsonAssert.setJsonvalidation(Boolean.TRUE);
                jsonAssert.setExpectedValue(var.getCheckContent());
                asserts.add(jsonAssert);
            }
        }
        return asserts;
    }

    private static List<ScriptJsonProcessor> convertJsonProcessor(PtsApiReturnVarRequest request) {
        List<ScriptJsonProcessor> processors = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getVars())) {
            return processors;
        }
        for(ReturnVarRequest var : request.getVars()) {
            if(!checkRegex(var.getParseExpress())) {
                ScriptJsonProcessor processor = new ScriptJsonProcessor();
                processor.setReferenceNames(var.getVarName());
                processor.setJsonPathExprs(var.getParseExpress());
                if (var.getMatchIndex() != null) {
                    processor.setMatchNumbers(var.getMatchIndex().toString());
                }
                processors.add(processor);
            }
        }
        return processors;
    }

    private static List<ScriptJsonProcessor> convertRegexExtractor(PtsApiReturnVarRequest request) {
        List<ScriptJsonProcessor> processors = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getVars())) {
            return processors;
        }
        for(ReturnVarRequest var : request.getVars()) {
            if(checkRegex(var.getParseExpress())) {
                ScriptJsonProcessor processor = new ScriptJsonProcessor();
                processor.setReferenceNames(var.getVarName());
                processor.setJsonPathExprs(var.getParseExpress());
                if (var.getMatchIndex() != null) {
                    processor.setMatchNumbers(var.getMatchIndex().toString());
                }
                processor.setUseHeader(var.getVarSource().equals(PtsVarSourceEnum.BODY_JSON.getName()) ? "false" : "true");
                processors.add(processor);
            }
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
            scriptData.setFormat(csvRequest.getParams());
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

    private static Boolean checkRegex(String expression) {
        return StringUtils.containsAny(expression, "*", "(");
    }
}
