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
        System.out.println(parseJmxString("{\"counters\":[{\"end\":\"999999\",\"format\":\"000000\",\"incr\":\"1\",\"name\":\"_reqJnl\",\"start\":\"1\"}],\"dataSource\":{\"csvs\":[{\"fileName\":\"medNo_prodAcctNo_custNo.txt\",\"ingoreFirstLine\":false,\"params\":\"medNo,prodAcctNo,custNo\"},{\"fileName\":\"belgOrg_relaTeller.txt\",\"ingoreFirstLine\":false,\"params\":\"belgOrg,relaTeller\"}]},\"existError\":false,\"globalHeader\":{\"headers\":[{\"key\":\"Accept\",\"value\":\"application/json, text/plain, */*\"},{\"key\":\"Accept-Language\",\"value\":\"zh-CN,zh;q=0.9\"},{\"key\":\"Content-Type\",\"value\":\"application/json;charset=UTF-8\"}]},\"globalHttp\":{\"contentEncoding\":\"UTF-8\",\"domain\":\"dore-adps.hzbank-core-load\",\"path\":\"/agdp/ADPS/AAGDP15302/v1.0.0\",\"port\":\"12002\",\"protocol\":\"http\"},\"links\":[{\"apis\":[{\"apiName\":\"个人现金取款\",\"apiType\":\"HTTP\",\"base\":{\"allowForward\":true,\"keepAlive\":true,\"requestMethod\":\"POST\",\"requestUrl\":\"http://dore-adps.hzbank-core-load:12002/agdp/ADPS/AAGDP15302/v1.0.0\"},\"beanShellPre\":{\"script\":[\"String reqJnl=\\\"${__time(,)}${_reqJnl}\\\";\\nvars.put(\\\"reqJnl\\\",reqJnl);\"]},\"body\":{\"forms\":[],\"rawData\":\"{\\r\\n  \\\"reqPubHead\\\": {\\r\\n    \\\"commtVerNo\\\": \\\"1.0.0\\\",\\r\\n    \\\"transCode\\\": \\\"GDEP0061\\\",\\r\\n    \\\"orgnlSrlNo\\\": \\\"1\\\",\\r\\n    \\\"transType\\\": \\\"1\\\",\\r\\n    \\\"initiatorInfo\\\": {\\r\\n      \\\"initrJnl\\\": \\\"1\\\",\\r\\n      \\\"initrDt\\\": \\\"2022-12-12\\\",\\r\\n      \\\"initrTm\\\": \\\"10:10:10\\\",\\r\\n      \\\"initrId\\\": \\\"DPCM\\\",\\r\\n      \\\"initrIp\\\": \\\"127.0.0.1\\\",\\r\\n      \\\"initrChnlNo\\\": \\\"9901\\\",\\r\\n      \\\"initrTermnNo\\\": \\\"A001\\\",\\r\\n      \\\"initrPhysAddr\\\": \\\"127.0.0.1\\\",\\r\\n      \\\"initrOperNo\\\": \\\"${relaTeller}\\\",\\r\\n      \\\"initrOrg\\\": \\\"${belgOrg}\\\"\\r\\n    },\\r\\n    \\\"requesterInfo\\\": {\\r\\n      \\\"reqJnl\\\": \\\"${reqJnl}\\\",\\r\\n      \\\"reqDt\\\": \\\"2022-12-12\\\",\\r\\n      \\\"reqTm\\\": \\\"10:10:10\\\",\\r\\n      \\\"reqIp\\\": \\\"127.0.0.1\\\",\\r\\n      \\\"reqId\\\": \\\"pspb\\\"\\r\\n    },\\r\\n    \\\"transLang\\\": \\\"ch-zh\\\",\\r\\n    \\\"oblgField\\\": \\\"预留字段\\\",\\r\\n    \\\"innerInfo\\\": {\\r\\n      \\\"servJnl\\\": \\\"1\\\",\\r\\n      \\\"servDt\\\": \\\"2022-12-12\\\",\\r\\n      \\\"bizDt\\\": \\\"2022-12-12\\\",\\r\\n      \\\"servTm\\\": \\\"10:10:10\\\",\\r\\n      \\\"subTranscSrlNo\\\": \\\"122\\\",\\r\\n      \\\"invokeTraceNo\\\": \\\"1111\\\",\\r\\n      \\\"invokeOrderNo\\\": \\\"1111\\\"\\r\\n    }\\r\\n  },\\r\\n  \\\"reqBizHead\\\": {\\r\\n    \\\"bizPeriodNo\\\": \\\"9999\\\",\\r\\n    \\\"bizBatchNo\\\": \\\"1\\\",\\r\\n    \\\"tellerInfos\\\": {\\r\\n      \\\"tellerList\\\": [\\r\\n        {\\r\\n          \\\"operrType\\\": \\\"01\\\",\\r\\n          \\\"operrNo\\\": \\\"${relaTeller}\\\"\\r\\n        }\\r\\n      ],\\r\\n      \\\"operrQty\\\": \\\"1\\\"\\r\\n    },\\r\\n    \\\"organizerInfos\\\": {\\r\\n      \\\"orgQty\\\": \\\"1\\\",\\r\\n      \\\"organList\\\": [\\r\\n        {\\r\\n          \\\"orgType\\\": \\\"01\\\",\\r\\n          \\\"orgNo\\\": \\\"${belgOrg}\\\"\\r\\n        }\\r\\n      ]\\r\\n    },\\r\\n    \\\"relaSrlNo\\\": \\\"1221\\\"\\r\\n  },\\r\\n  \\\"reqBody\\\": {\\r\\n  \\t\\\"initrMedNo\\\" : \\\"${medNo}\\\",\\r\\n\\t\\\"prodAcctNo\\\" : \\\"${prodAcctNo}\\\",\\r\\n\\t\\\"transAmt\\\" : \\\"1.00\\\",\\r\\n\\t\\\"agentFlag\\\" : \\\"0\\\",\\r\\n\\t\\\"custNo\\\" : \\\"${custNo}\\\",\\r\\n\\t\\\"cashTranFlag\\\" : \\\"0\\\",\\r\\n\\t\\\"currency\\\" : \\\"00\\\",\\r\\n\\t\\\"vouNo\\\" : \\\"00000019\\\",\\r\\n\\t\\\"vouType\\\" : \\\"0019\\\"\\r\\n  }\\r\\n}\"},\"checkAssert\":{\"asserts\":[{\"checkCondition\":\"等于\",\"checkContent\":\"0000000000000\",\"checkObject\":\"$.respPubHead.transStat\",\"checkPointType\":\"出参\"}]},\"header\":{\"headers\":[]},\"returnVar\":{\"vars\":[]},\"timer\":{}}],\"linkName\":\"基准测试_个人现金取款\"}],\"message\":[],\"preLink\":{\"apis\":[]},\"processName\":\"测试计划\"}\n"));
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
                        PtsJmxBuildUtil.buildJavaSampler(threadGroupHashEle, apiRequest.getApiName(), convertKeyValue2Map(apiRequest.getHeader().getHeaders()));
                    }
                }
            }
        }
        //创建查看结果树
        PtsJmxBuildUtil.buildViewResultsTree(testPlan);
        return XmlUtil.format(PtsJmxBuildUtil.getDocumentStr(document));
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
