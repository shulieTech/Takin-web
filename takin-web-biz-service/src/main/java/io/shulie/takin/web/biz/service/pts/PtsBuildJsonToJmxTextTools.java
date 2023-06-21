package io.shulie.takin.web.biz.service.pts;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.enums.pts.*;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonAssert;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonProcessor;
import io.shulie.takin.cloud.common.script.jmeter.ScriptKeyValueParam;
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
        System.out.println(parseJmxString("{\"id\":\"1006\",\"globalHttp\":{},\"counters\":[],\"processName\":\"我的下午验证\",\"links\":[{\"linkName\":\"线程组\",\"apis\":[{\"apiName\":\"takin-web-get\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"requestMethod\":\"GET\",\"requestTimeout\":null,\"requestUrl\":\"http://192.168.1.201:80/takin-web/api/serverConfig\"},\"body\":{},\"checkAssert\":{\"asserts\":[{\"checkPointType\":\"响应状态码\",\"checkObject\":\"状态码\",\"checkCondition\":\"等于\",\"checkContent\":\"200\"},{\"checkPointType\":\"响应Body\",\"checkObject\":\"整个body\",\"checkCondition\":\"包含\",\"checkContent\":\"domain\"},{\"checkPointType\":\"响应Body\",\"checkObject\":\"整个body\",\"checkCondition\":\"正则匹配\",\"checkContent\":\"\\\\\\\"loginType\\\\\\\":(.*?)\"},{\"checkPointType\":\"出参\",\"checkObject\":\"$.data.loginType\",\"checkCondition\":\"等于\",\"checkContent\":\"1\"}]},\"header\":{\"headers\":[{\"key\":\"env-code\",\"value\":\"test\"},{\"key\":\"id\",\"value\":\"${id1}\"},{\"key\":\"tenant-code\",\"value\":\"gc\"}]},\"returnVar\":{\"vars\":[]},\"timer\":{\"delay\":null},\"beanShellPre\":{\"script\":[null]},\"beanShellPost\":{\"script\":[null]}},{\"apiName\":\"Java请求1\",\"apiType\":\"JAVA\",\"base\":{\"requestUrl\":\"com.demo.test.JavaRequestTest\"},\"param\":{\"params\":[{\"paramCNDesc\":\"测试参数\",\"paramName\":\"test_aa\",\"paramValue\":\"aaa1\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false,\"sortNum\":1},{\"paramCNDesc\":\"我的参数\",\"paramName\":\"my_param\",\"paramValue\":\"java-request-test1_${count1}\",\"paramType\":\"file\",\"require\":false,\"allowEdit\":false,\"sortNum\":2}]},\"checkAssert\":{\"asserts\":[{\"checkPointType\":\"出参\",\"checkObject\":\"$.code\",\"checkCondition\":\"等于\",\"checkContent\":\"200\"}]}},{\"apiName\":\"user-register-post\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"requestMethod\":\"POST\",\"requestTimeout\":null,\"requestUrl\":\"http://192.168.1.222:28881/gateway/api/register\"},\"body\":{\"rawData\":\"{\\n    \\\\\\\"mobile\\\\\\\":\\\\\\\"15558172201\\\\\\\",\\n    \\\\\\\"password\\\\\\\":\\\\\\\"123456\\\\\\\",\\n    \\\\\\\"nickName\\\\\\\":\\\\\\\"${var1}\\\\\\\",\\n    \\\\\\\"email\\\\\\\":\\\\\\\"${var2}_${var3}@qq.com\\\\\\\",\\n    \\\\\\\"birthDay\\\\\\\":\\\\\\\"2000-11-05\\\\\\\",\\n    \\\\\\\"provinceName\\\\\\\":\\\\\\\"浙江\\\\\\\",\\n    \\\\\\\"cityName\\\\\\\":\\\\\\\"杭州\\\\\\\"\\n }\",\"contentType\":\"JSON\"},\"checkAssert\":{\"asserts\":[]},\"header\":{\"headers\":[{\"key\":\"Content-Type\",\"value\":\"application/json\"}]},\"returnVar\":{\"vars\":[{\"testName\":\"JSON提取器\",\"varName\":\"saveCode\",\"varSource\":\"Body:JSON|TEXT\",\"parseExpress\":\"$.data.code\",\"matchIndex\":null}]},\"timer\":{\"delay\":\"2000\"},\"beanShellPre\":{\"script\":[\"var name=js\"]},\"beanShellPost\":{\"script\":[\"var newName = zxzghr\"]}},{\"apiName\":\"Java请求2\",\"apiType\":\"JAVA\",\"base\":{\"requestUrl\":\"com.demo.test.JavaRequestTest\"},\"param\":{\"params\":[{\"paramCNDesc\":\"测试参数\",\"paramName\":\"test_aa\",\"paramValue\":\"aaa2\",\"paramType\":\"text\",\"require\":true,\"allowEdit\":false,\"sortNum\":1},{\"paramCNDesc\":\"我的参数\",\"paramName\":\"my_param\",\"paramValue\":\"java-request-test_${saveCode}_${count2)\",\"paramType\":\"file\",\"require\":false,\"allowEdit\":false,\"sortNum\":2}]},\"checkAssert\":{\"asserts\":[]}},{\"apiName\":\"qq\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"requestMethod\":\"GET\",\"requestTimeout\":3000,\"requestUrl\":\"http://www.qq.com/\"},\"body\":{},\"checkAssert\":{\"asserts\":[]},\"header\":{\"headers\":[]},\"returnVar\":{\"vars\":[]},\"timer\":{\"delay\":null},\"beanShellPre\":{\"script\":[null]},\"beanShellPost\":{\"script\":[null]}}]}],\"dataSource\":{\"csvs\":[{\"fileName\":\"user1.csv\",\"params\":\"id1,name1\",\"ingoreFirstLine\":false},{\"fileName\":\"user2.txt\",\"params\":\"id1,name2\",\"ingoreFirstLine\":true}]},\"globalHeader\":{\"headers\":[{\"key\":\"header3\",\"value\":\"value3\"},{\"key\":\"header2\",\"value\":\"value2\"},{\"key\":\"header1\",\"value\":\"value1\"}]},\"userVars\":[{\"key\":\"var2\",\"value\":\"value2\",\"desc\":\"变量2\"},{\"key\":\"var1\",\"value\":\"value11\",\"desc\":\"变脸1\"},{\"key\":\"var3\",\"value\":\"value3\",\"desc\":\"变脸3\"}]}\n"));
    }

    public static String parseJmxString(String jsonString) throws Exception {
        PtsSceneRequest sceneRequest = JSON.parseObject(jsonString, PtsSceneRequest.class);
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        //创建基础脚本
        org.dom4j.Element rootHashEle = PtsJmxBuildUtil.buildBase(document);
        //创建测试计划
        org.dom4j.Element testPlan = PtsJmxBuildUtil.buildTestPlan(rootHashEle, sceneRequest.getProcessName());
        //用户自定义变量
        PtsJmxBuildUtil.buildUserVars(testPlan, convertKeyValue2Param(sceneRequest.getUserVars()));
        //数据源
        PtsDataSourceRequest dataSource = sceneRequest.getDataSource();
        PtsJmxBuildUtil.buildCsvData(testPlan, convertCsvData(dataSource.getCsvs()));
        //全局HTTP请求头
        PtsApiHeaderRequest globalHeader = sceneRequest.getGlobalHeader();
        PtsJmxBuildUtil.buildHttpHeader(testPlan, convertHeader(globalHeader.getHeaders()));
        //全局HTTP请求
        PtsGlobalHttpRequest globalHttp = sceneRequest.getGlobalHttp();
        if(StringUtils.isNotBlank(globalHttp.getProtocol()) || StringUtils.isNotBlank(globalHttp.getDomain())
                || StringUtils.isNotBlank(globalHttp.getPort()) || StringUtils.isNotBlank(globalHttp.getPath())
                || StringUtils.isNotBlank(globalHttp.getContentEncoding())) {
            PtsJmxBuildUtil.buildHttpDefault(testPlan, JSONObject.parseObject(JSON.toJSONString(globalHttp)));
        }
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
                if(link == null) {
                    continue;
                }
                org.dom4j.Element threadGroupHashEle = PtsJmxBuildUtil.buildThreadGroup(testPlan, PtsThreadGroupTypeEnum.getByType(link.getLinkType()), link.getEnabled(), link.getLinkName());
                for(PtsApiRequest apiRequest : link.getApis()) {
                    if(apiRequest == null) {
                        continue;
                    }
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
                        String contentType = apiRequest.getBody().getContentType();
                        paramMap.put("doMultipartPost", StringUtils.equals(contentType, PtsContentTypeEnum.FORM.getType()) ? "true" : "false");
                        org.dom4j.Element sampler = PtsJmxBuildUtil.buildHttpSampler(threadGroupHashEle, apiRequest.getApiName(),
                                apiRequest.getEnabled(), url, apiRequest.getBase().getRequestMethod(),
                                JSON.toJSONString(apiRequest.getBody().getForms()),
                                apiRequest.getBody().getRawData(),
                                paramMap);
                        //POST-JSON请求，自动加Content-Type
                        if(StringUtils.equals(contentType, PtsContentTypeEnum.JSON.getType())) {
                            apiRequest.getHeader().getHeaders().add(new KeyValueRequest("Content-Type", PtsContentTypeEnum.JSON.getValue()));
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
                        //BeanShellPost
                        PtsJmxBuildUtil.buildApiBeanshellPost(sampler, apiRequest.getBeanShellPost().getScript());
                    } else if(SamplerTypeEnum.JAVA.getType().equals(apiRequest.getApiType())) {
                        //创建Java请求
                        if(StringUtils.isBlank(apiRequest.getBase().getRequestUrl())) {
                            continue;
                        }
                        org.dom4j.Element sampler = PtsJmxBuildUtil.buildJavaSampler(threadGroupHashEle, apiRequest.getApiName(), apiRequest.getEnabled(), apiRequest.getBase().getRequestUrl(), convertJavaParam2Map(apiRequest.getParam().getParams()));
                        //断言
                        PtsJmxBuildUtil.buildResponseAssert(sampler, convertResponseAssert(apiRequest.getCheckAssert()));
                        PtsJmxBuildUtil.buildJsonAssert(sampler, convertJsonAssert(apiRequest.getCheckAssert()));
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
        requestList.forEach(data -> {
            if(StringUtils.isNotBlank(data.getParamName())) {
                dataMap.put(data.getParamName(), data.getParamValue());
            }
        });
        return dataMap;
    }

    private static List<ScriptKeyValueParam> convertKeyValue2Param(List<VarsKeyValueRequest> requestList) {
        List<ScriptKeyValueParam> dataList = new ArrayList<>();
        if(CollectionUtils.isEmpty(requestList)) {
            return dataList;
        }
        requestList.forEach(data -> {
            if(StringUtils.isNotBlank(data.getKey())) {
                ScriptKeyValueParam param = new ScriptKeyValueParam();
                param.setKey(data.getKey());
                param.setValue(data.getValue());
                param.setDesc(data.getDesc());
                dataList.add(param);
            }
        });
        return dataList;
    }

    private static List<ScriptResponseAssert> convertResponseAssert(PtsApiAssertRequest request) {
        List<ScriptResponseAssert> asserts = new ArrayList<>();
        if(request == null || CollectionUtils.isEmpty(request.getAsserts())) {
            return asserts;
        }
        for(AssertRequest var : request.getAsserts()) {
            if(StringUtils.isBlank(var.getCheckPointType())
                    || StringUtils.isBlank(var.getCheckCondition())
                    || StringUtils.isBlank(var.getCheckContent())) {
                continue;
            }
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
            if(StringUtils.isBlank(var.getCheckPointType())
                    || StringUtils.isBlank(var.getCheckObject())
                    || StringUtils.isBlank(var.getCheckCondition())
                    || StringUtils.isBlank(var.getCheckContent())) {
                continue;
            }
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
            if(!checkRegex(var.getParseExpress()) && StringUtils.isNotBlank(var.getVarName())) {
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
            if(checkRegex(var.getParseExpress())
                    && StringUtils.isNotBlank(var.getVarName())
                    && StringUtils.isNotBlank(var.getVarSource())) {
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
            if(StringUtils.isBlank(csvRequest.getFileName())) {
                continue;
            }
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
            if(StringUtils.isBlank(header.getKey())) {
                continue;
            }
            ScriptHeader scriptHeader = new ScriptHeader();
            scriptHeader.setKey(header.getKey());
            scriptHeader.setValue(header.getValue());
            scriptHeaders.add(scriptHeader);
        }
        return scriptHeaders;
    }

    private static Boolean checkRegex(String expression) {
        return StringUtils.containsAny(expression, "*", "(");
    }
}
