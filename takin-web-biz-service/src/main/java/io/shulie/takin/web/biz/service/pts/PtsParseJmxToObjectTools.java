package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.common.constants.JmxConstant;
import io.shulie.takin.cloud.common.enums.pts.*;
import io.shulie.takin.cloud.common.utils.PtsJmxParseUtil;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.K;

import java.util.*;
import java.util.stream.Collectors;

public class PtsParseJmxToObjectTools {

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(parseJmxFile("/Users/xiaoshu/Desktop/untitled.jmx", false)));
    }

    public static void removeSamplerNodeChildren(List<ScriptNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        Iterator<ScriptNode> iterator = nodes.iterator();
        while(iterator.hasNext()) {
            ScriptNode scriptNode = iterator.next();
            if(!scriptNode.getEnabled() || (
                    scriptNode.getType() != NodeTypeEnum.TEST_PLAN
                    && scriptNode.getType() != NodeTypeEnum.THREAD_GROUP
                    && scriptNode.getType() != NodeTypeEnum.CONTROLLER
                    && scriptNode.getType() != NodeTypeEnum.SAMPLER)) {
                iterator.remove();
            }
            removeSamplerNodeChildren(scriptNode.getChildren());
        }
    }

    public static PtsSceneResponse parseJmxFile(String fileFullPath, boolean removeExtra) {
        PtsSceneResponse response = new PtsSceneResponse();
        List<ScriptNode> nodeList = PtsJmxParseUtil.buildNodeTree(fileFullPath);
        if(removeExtra) {
            removeSamplerNodeChildren(nodeList);
        }
        if(CollectionUtils.isNotEmpty(nodeList)) {
            convert(response, nodeList.get(0));
        }
        return response;
    }

    private static void convert(PtsSceneResponse response, ScriptNode node) {
        if(node == null) {
            return;
        }
        fillData(response, node);
        if(CollectionUtils.isNotEmpty(node.getChildren())) {
            node.getChildren().forEach(children -> convert(response, children));
        }
    }

    private static void fillData(PtsSceneResponse response, ScriptNode node) {
        if(node.getType() == NodeTypeEnum.TEST_PLAN) {
            response.setProcessName(node.getTestName());
        } else if(node.getType() == NodeTypeEnum.DATASET) {
            //处理csv文件
            fillPtsDataSourceRequest(response.getDataSource(), node);
        } else if(node.getType() == NodeTypeEnum.COUNTER) {
            //处理counter计数器
            fillPtsCounter(response, node);
        } else if(node.getType() == NodeTypeEnum.ARGUMENTS) {
            //处理自定义常量
            fillUserVars(response, node);
        } else if(node.getType() == NodeTypeEnum.THREAD_GROUP) {
            PtsLinkRequest linkRequest = new PtsLinkRequest();
            linkRequest.setLinkName(node.getTestName());
            linkRequest.setLinkType(parseLinkTypeByNodeName(node.getName()));
            linkRequest.setEnabled(node.getEnabled());
            response.getLinks().add(linkRequest);
        } else if(node.getType() == NodeTypeEnum.HEADERMANAGER) {
            //处理全局请求头
            if(node.getParentType() != NodeTypeEnum.SAMPLER) {
                PtsApiHeaderRequest globalHeader = response.getGlobalHeader();
                fillRequestHeaderData(globalHeader.getHeaders(), node.getProps());
            }
        } else if(node.getType() == NodeTypeEnum.TIMER) {
            if(node.getParentType() != NodeTypeEnum.SAMPLER) {
                response.getMessage().add(String.format("解析错误：定时器【】未定义在接口下", node.getTestName()));
            }
        } else if(node.getType() == NodeTypeEnum.BEANSHELLPRE) {
            if(node.getParentType() != NodeTypeEnum.SAMPLER) {
                response.getMessage().add(String.format("解析错误：BeanShell预处理程序【】未定义在接口下", node.getTestName()));
            }
        } else if(node.getType() == NodeTypeEnum.CONFIG_TEST_ELEMENT) {
            if(node.getParentType() != NodeTypeEnum.TEST_PLAN) {
                response.getMessage().add(String.format("解析错误：全局HTTP【】未定义在测试计划下", node.getTestName()));
                return;
            }
            //处理全局HTTP
            PtsGlobalHttpRequest globalHttp = response.getGlobalHttp();
            if(StringUtils.isBlank(globalHttp.getDomain())) {
                globalHttp.setDomain(node.getProps().get("HTTPSampler.domain"));
            }
            if(StringUtils.isBlank(globalHttp.getProtocol())) {
                globalHttp.setProtocol(node.getProps().get("HTTPSampler.protocol"));
            }
            if(StringUtils.isBlank(globalHttp.getPort())) {
                globalHttp.setPort(node.getProps().get("HTTPSampler.port"));
            }
            if(StringUtils.isBlank(globalHttp.getPath())) {
                globalHttp.setPath(node.getProps().get("HTTPSampler.path"));
            }
            if(StringUtils.isBlank(globalHttp.getContentEncoding())) {
                globalHttp.setContentEncoding(node.getProps().get("HTTPSampler.contentEncoding"));
            }
            response.setGlobalHttp(globalHttp);
        } else if(node.getType() == NodeTypeEnum.SAMPLER) {
            if(node.getParentType() != NodeTypeEnum.THREAD_GROUP) {
                response.getMessage().add(String.format("解析错误：接口【】未定义在线程组下", node.getTestName()));
                return;
            }
            if(node.getSamplerType() == SamplerTypeEnum.HTTP) {
                PtsLinkRequest linkRequest = response.getLinks().get(response.getLinks().size() - 1);
                PtsApiRequest apiRequest = new PtsApiRequest();
                apiRequest.setApiType(SamplerTypeEnum.HTTP.getType());
                apiRequest.setApiName(node.getTestName());
                apiRequest.setEnabled(node.getEnabled());
                //处理base内容
                apiRequest.getBase().setRequestUrl(concatRequestUrl(node.getProps()));
                apiRequest.getBase().setRequestMethod(node.getProps().get(JmxConstant.httpSamplerMethod));
                String responseTimeout = node.getProps().get(JmxConstant.httpSamplerResponseTimeout);
                if(StringUtils.isNotBlank(responseTimeout)) {
                    apiRequest.getBase().setRequestTimeout(Integer.parseInt(responseTimeout));
                }
                apiRequest.getBase().setAllowForward(Boolean.parseBoolean(node.getProps().get(JmxConstant.httpSamplerFollowRedirects)));
                apiRequest.getBase().setKeepAlive(Boolean.parseBoolean(node.getProps().get(JmxConstant.httpSamplerUseKeepalive)));
                //处理入参
                fillRequestBodyData(apiRequest.getBody(), node.getProps().get(JmxConstant.httpSamplerdoMultipartPost), node.getProps());
                if(CollectionUtils.isNotEmpty(node.getChildren())) {
                    //处理定时器
                    List<ScriptNode> timeList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.TIMER).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(timeList)) {
                        if(timeList.size() > 1) {
                            response.getMessage().add(String.format("接口【】下定义了多个定时器，只生效第一个", apiRequest.getApiName()));
                        }
                        fillTimerData(apiRequest.getTimer(), timeList.get(0));
                    }
                    //处理BeanShell前置处理器
                    List<ScriptNode> beanshellPreList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.BEANSHELLPRE).collect(Collectors.toList());
                    fillBeanShellPreData(apiRequest.getBeanShellPre(), beanshellPreList);
                    //处理请求头
                    List<ScriptNode> headerList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.HEADERMANAGER).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(headerList)){
                        if(headerList.size() > 1) {
                            response.getMessage().add(String.format("接口【】下定义了多个HeaderManager，只生效第一个", apiRequest.getApiName()));
                        }
                        fillRequestHeaderData(apiRequest.getHeader().getHeaders(), headerList.get(0).getProps());
                    }
                    //处理JSON提取器
                    List<ScriptNode> jsonProcessList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.POSTPROCESSOR || data.getType() == NodeTypeEnum.REGEXEXTRACTOR).collect(Collectors.toList());
                    fillExtractorData(apiRequest.getReturnVar(), jsonProcessList);
                    //处理断言
                    List<ScriptNode> assertList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.ASSERTION).collect(Collectors.toList());
                    fillAssertionData(apiRequest.getCheckAssert(), assertList);
                    //处理BeanShell后置处理器
                    List<ScriptNode> beanshellPostList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.POSTPROCESSOR).collect(Collectors.toList());
                    fillBeanShellPostData(apiRequest.getBeanShellPost(), beanshellPostList);
                }
                linkRequest.getApis().add(apiRequest);
            } else if (node.getSamplerType() == SamplerTypeEnum.JAVA) {
                PtsLinkRequest linkRequest = response.getLinks().get(response.getLinks().size() - 1);
                PtsApiRequest apiRequest = new PtsApiRequest();
                apiRequest.setApiType(SamplerTypeEnum.JAVA.getType());
                apiRequest.setApiName(node.getTestName());
                apiRequest.setEnabled(node.getEnabled());
                PtsApiBaseRequest baseRequest = new PtsApiBaseRequest();
                baseRequest.setRequestUrl(node.getProps().get("classname"));
                apiRequest.setBase(baseRequest);
                fillJavaParamData(apiRequest.getParam(), node.getProps());
                if(CollectionUtils.isNotEmpty(node.getChildren())) {
                    //处理断言
                    List<ScriptNode> assertList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.ASSERTION).collect(Collectors.toList());
                    fillAssertionData(apiRequest.getCheckAssert(), assertList);
                }
                linkRequest.getApis().add(apiRequest);
            }
        }
    }

    private static String parseLinkTypeByNodeName(String nodeName) {
        String linkType = PtsThreadGroupTypeEnum.NORMAL.getType();
        if(nodeName.equals("SetupThreadGroup")) {
            linkType = PtsThreadGroupTypeEnum.SETUP.getType();
        } else if(nodeName.equals("PostThreadGroup")) {
            linkType = PtsThreadGroupTypeEnum.TEARDOWN.getType();
        }
        return linkType;
    }

    private static void fillTimerData(PtsApiTimerRequest timerRequest, ScriptNode scriptNode) {
        timerRequest.setDelay(scriptNode.getProps().get("ConstantTimer.delay"));
    }

    private static void fillUserVars(PtsSceneResponse response, ScriptNode scriptNode) {
        List<VarsKeyValueRequest> dataList = new ArrayList<>();
        Set<String> rewriteKey = new HashSet<>();
        scriptNode.getProps().forEach((key, value) -> {
            String[] args = value.split(JmxConstant.valueDescSpilt);
            String realKey = key.substring(JmxConstant.argumentNamePrefix.length());
            if(args.length > 1) {
                dataList.add(new VarsKeyValueRequest(realKey, args[0], args[1]));
                rewriteKey.add(realKey);
            } else if (args.length > 0) {
                dataList.add(new VarsKeyValueRequest(realKey, args[0], ""));
                rewriteKey.add(realKey);
            }
        });
        //用户变量，后出现的覆盖原来的
        if(CollectionUtils.isNotEmpty(response.getUserVars()) && rewriteKey.size() > 0) {
            Iterator<VarsKeyValueRequest> iterator = response.getUserVars().iterator();
            while (iterator.hasNext()) {
                VarsKeyValueRequest request = iterator.next();
                if(rewriteKey.contains(request.getKey())) {
                    iterator.remove();
                }
            }
        }
        response.getUserVars().addAll(dataList);
    }

    private static void fillPtsCounter(PtsSceneResponse response, ScriptNode scriptNode) {
        if(!scriptNode.getName().equals("CounterConfig")) {
            return;
        }
        PtsCounterRequest counter = new PtsCounterRequest();
        counter.setStart(scriptNode.getProps().get("CounterConfig.start"));
        counter.setEnd(scriptNode.getProps().get("CounterConfig.end"));
        counter.setIncr(scriptNode.getProps().get("CounterConfig.incr"));
        counter.setName(scriptNode.getProps().get("CounterConfig.name"));
        counter.setFormat(scriptNode.getProps().get("CounterConfig.format"));
        response.getCounters().add(counter);
    }

    private static void fillPtsDataSourceRequest(PtsDataSourceRequest dataSource, ScriptNode scriptNode) {
        if(!scriptNode.getName().equals("CSVDataSet")) {
            return;
        }
        PtsCsvRequest csv = new PtsCsvRequest();
        csv.setFileName(convertFileName(scriptNode.getProps().get("filename")));
        csv.setIngoreFirstLine(Boolean.parseBoolean(scriptNode.getProps().get("ignoreFirstLine")));
        csv.setParams(scriptNode.getProps().get("variableNames"));
        dataSource.getCsvs().add(csv);
    }

    private static String convertFileName(String fullFileName) {
        int pos = fullFileName.lastIndexOf("/");
        if(pos > -1) {
            return fullFileName.substring(pos + 1);
        }
        pos = fullFileName.lastIndexOf("\\");
        if(pos > -1) {
            return fullFileName.substring(pos + 1);
        }
        return fullFileName;
    }

    private static List<PtsCsvLineRequest> convertCsvLines(String variableNames) {
        List<PtsCsvLineRequest> params = new ArrayList<>();
        if(StringUtils.isBlank(variableNames)) {
            return params;
        }
        String[] vars = variableNames.split(",");
        int index= 0;
        for(String var : vars) {
            index++;
            if(StringUtils.isBlank(var)) {
                continue;
            }
            PtsCsvLineRequest param = new PtsCsvLineRequest();
            param.setLineNumber(index);
            param.setParamsName(var);
            params.add(param);
        }
        return params;
    }

    private static void fillRequestBodyData(PtsApiBodyRequest bodyRequest, String doMultipartPost, Map<String, String> propsMap) {
        if(propsMap.containsKey("Argument.value")) {
            bodyRequest.setRawData(propsMap.get("Argument.value"));
            bodyRequest.setContentType(PtsContentTypeEnum.JSON.getType());
        } else {
            List<KeyValueRequest> forms = new ArrayList<>();
            Set<Map.Entry<String, String>> entries = propsMap.entrySet();
            for(Map.Entry<String, String> entry : entries) {
                if(entry.getKey().startsWith(JmxConstant.argumentNamePrefix)) {
                    KeyValueRequest form = new KeyValueRequest();
                    form.setKey(entry.getKey().substring(JmxConstant.argumentNamePrefix.length()));
                    form.setValue(entry.getValue());
                    forms.add(form);
                }
            }
            if(StringUtils.equals(doMultipartPost, "true")) {
                bodyRequest.setContentType(PtsContentTypeEnum.FORM.getType());
            } else {
                bodyRequest.setContentType(PtsContentTypeEnum.URL_ENCODE.getType());
            }
            bodyRequest.setForms(forms);
        }
    }

    private static void fillRequestHeaderData(List<KeyValueRequest> headers, Map<String, String> propsMap) {
        //请求头，如果已存在，则忽略后续的
        if(MapUtils.isEmpty(propsMap)) {
            return;
        }
        Set<String> existSet = new HashSet<>();
        headers.stream().forEach(data -> existSet.add(data.getKey()));
        propsMap.forEach((key, value) -> {
            if(!existSet.contains(key)) {
                headers.add(new KeyValueRequest(key, value));
            }
        });
    }

    private static void fillJavaParamData(PtsApiParamRequest paramRequest, Map<String, String> propsMap) {
        Set<Map.Entry<String, String>> entries = propsMap.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            if(entry.getKey().startsWith(JmxConstant.argumentNamePrefix)) {
                paramRequest.getParams().add(new JavaParamRequest(entry.getKey().substring(JmxConstant.argumentNamePrefix.length()), entry.getValue()));
            }
        }
    }

    private static void fillExtractorData(PtsApiReturnVarRequest returnVar, List<ScriptNode> jsonProcessList) {
        if(CollectionUtils.isEmpty(jsonProcessList)) {
            return;
        }
        List<ReturnVarRequest> varList = returnVar.getVars();
        for(ScriptNode scriptNode : jsonProcessList) {
            ReturnVarRequest var = new ReturnVarRequest();
            var.setTestName(scriptNode.getTestName());
            if("JSONPostProcessor".equals(scriptNode.getName())) {
                var.setVarName(scriptNode.getProps().get("JSONPostProcessor.referenceNames"));
                var.setParseExpress(scriptNode.getProps().get("JSONPostProcessor.jsonPathExprs"));
                String index = scriptNode.getProps().get("JSONPostProcessor.match_numbers");
                if (StringUtils.isNotBlank(index)) {
                    var.setMatchIndex(Integer.parseInt(index));
                }
                var.setVarSource(PtsVarSourceEnum.BODY_JSON.getName());
                varList.add(var);
            } else if("RegexExtractor".equals(scriptNode.getName())) {
                var.setVarName(scriptNode.getProps().get("RegexExtractor.refname"));
                var.setParseExpress(scriptNode.getProps().get("RegexExtractor.regex"));
                String useHeaders = scriptNode.getProps().get("RegexExtractor.useHeaders");
                if(!StringUtils.equals(useHeaders, "true") && !StringUtils.equals(useHeaders, "false")) {
                    continue;
                }
                String index = scriptNode.getProps().get("RegexExtractor.match_number");
                if (StringUtils.isNotBlank(index)) {
                    var.setMatchIndex(Integer.parseInt(index));
                }
                var.setVarSource(useHeaders.equals("true") ? PtsVarSourceEnum.HEADERKV.getName() : PtsVarSourceEnum.BODY_JSON.getName());
                varList.add(var);
            }
        }
    }

    private static void fillAssertionData(PtsApiAssertRequest assertRequest, List<ScriptNode> assertList) {
        if(CollectionUtils.isEmpty(assertList)) {
            return;
        }
        List<AssertRequest> dataList = assertRequest.getAsserts();
        for(ScriptNode scriptNode : assertList) {
            AssertRequest var = new AssertRequest();
            if("JSONPathAssertion".equals(scriptNode.getName())) {
                var.setCheckPointType(PtsAssertTypeEnum.PARAMS.getName());
                var.setCheckObject(scriptNode.getProps().get("JSON_PATH"));
                var.setCheckCondition(PtsAssertConditionEnum.EQUAL.getName());
                var.setCheckContent(scriptNode.getProps().get("EXPECTED_VALUE"));
                dataList.add(var);
            } else if("ResponseAssertion".equals(scriptNode.getName())) {
                String testField = scriptNode.getProps().get("Assertion.test_field");
                PtsAssertTypeEnum typeEnum = PtsAssertTypeEnum.getByValue(testField);
                if(typeEnum == PtsAssertTypeEnum.RESPONSE_BODY || typeEnum == PtsAssertTypeEnum.RESPONSE_CODE) {
                    var.setCheckPointType(typeEnum.getName());
                    var.setCheckObject(typeEnum.getText());
                    String testType = scriptNode.getProps().get("Assertion.test_type");
                    PtsAssertConditionEnum conditionEnum = PtsAssertConditionEnum.getByValue(testType);
                    var.setCheckCondition(conditionEnum.getName());
                    var.setCheckContent(findAssertValue(scriptNode.getProps()));
                    dataList.add(var);
                }
            }
        }
    }

    private static void fillBeanShellPostData(PtsApiBeanShellPreRequest beanShellPost, List<ScriptNode> postList) {
        if(CollectionUtils.isEmpty(postList)) {
            return;
        }
        for(ScriptNode scriptNode : postList) {
            if(!scriptNode.getName().equals("BeanShellPostProcessor")) {
                continue;
            }
            beanShellPost.getScript().add(scriptNode.getProps().get("script"));
        }
    }

    private static void fillBeanShellPreData(PtsApiBeanShellPreRequest beanShellPre, List<ScriptNode> preList) {
        if(CollectionUtils.isEmpty(preList)) {
            return;
        }
        for(ScriptNode scriptNode : preList) {
            beanShellPre.getScript().add(scriptNode.getProps().get("script"));
        }
    }

    private static String findAssertValue(Map<String, String> props) {
        StringBuffer data = new StringBuffer();
        for(Map.Entry<String, String> dataMap : props.entrySet()) {
            if(!dataMap.getKey().startsWith("Assertion.")) {
                data.append(dataMap.getValue());
            }
        }
        return data.toString();
    }

    private static String concatRequestUrl(Map<String, String> props) {
        StringBuffer requestUrl = new StringBuffer();
        String protocol = props.get(JmxConstant.httpSamplerProtocol);
        if(StringUtils.isBlank(protocol)) {
            protocol = JmxConstant.defaultProtocol;
        }
        requestUrl.append(protocol);
        requestUrl.append("://");
        requestUrl.append(props.get(JmxConstant.httpSamplerDomain));
        String port = props.get(JmxConstant.httpSamplerPort);
        if(StringUtils.isNotBlank(port)) {
            requestUrl.append(":");
            requestUrl.append(port);
        }
        String path = props.get(JmxConstant.httpSamplerPath);
        if (StringUtils.isBlank(path)) {
            requestUrl.append("/");
        } else {
            if(!path.startsWith("/")) {
                requestUrl.append("/");
            }
            requestUrl.append(path);
        }
        return requestUrl.toString();
    }
}
