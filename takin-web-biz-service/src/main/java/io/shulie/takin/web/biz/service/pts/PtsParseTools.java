package io.shulie.takin.web.biz.service.pts;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.common.constants.JmxConstant;
import io.shulie.takin.cloud.common.utils.PtsJmxParseUtil;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PtsParseTools {

    private static final List<String> SUPPORT_CONDTION = Arrays.asList("1","2","8");

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(parseJmxFile("/Users/xiaoshu/Documents/okhttp3.jmx")));
    }

    public static PtsSceneResponse parseJmxFile(String fileFullPath) {
        PtsSceneResponse response = new PtsSceneResponse();
        List<ScriptNode> nodeList = PtsJmxParseUtil.buildNodeTree(fileFullPath);
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
        } else if(node.getType() == NodeTypeEnum.THREAD_GROUP) {
            PtsLinkRequest linkRequest = new PtsLinkRequest();
            linkRequest.setLinkName(node.getTestName());
            response.getLinks().add(linkRequest);
        } else if(node.getType() == NodeTypeEnum.SAMPLER) {
            if(response.getLinks().size() == 0) {
                response.getMessage().add(String.format("解析错误：接口【】未定义在线程组内", node.getTestName()));
                return;
            }
            if(node.getSamplerType() == SamplerTypeEnum.HTTP) {
                PtsLinkRequest linkRequest = response.getLinks().get(response.getLinks().size() - 1);
                PtsApiRequest apiRequest = new PtsApiRequest();
                apiRequest.setApiName(node.getTestName());
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
                fillRequestBodyData(apiRequest.getBody(), node.getProps());
                if(CollectionUtils.isNotEmpty(node.getChildren())) {
                    //处理请求头
                    List<ScriptNode> headerList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.HEADERMANAGER).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(headerList) && headerList.size() > 1) {
                        response.getMessage().add(String.format("接口【】下定义了多个HeaderManager，只生效第一个", apiRequest.getApiName()));
                    }
                    fillRequestHeaderData(apiRequest.getHeader(), headerList);
                    //处理JSON提取器
                    List<ScriptNode> jsonProcessList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.POSTPROCESSOR).collect(Collectors.toList());
                    fillJsonProcessData(apiRequest.getReturnVar(), jsonProcessList);
                    //处理断言
                    List<ScriptNode> assertList = node.getChildren().stream().filter(data -> data.getType() == NodeTypeEnum.ASSERTION).collect(Collectors.toList());
                    fillJsonProcessData(apiRequest.getCheckAssert(), assertList);
                }

                linkRequest.getApis().add(apiRequest);
            }
        }
    }

    private static void fillPtsDataSourceRequest(PtsDataSourceRequest dataSource, ScriptNode scriptNode) {
        if(!scriptNode.getName().equals("CSVDataSet")) {
            return;
        }
        PtsCsvRequest csv = new PtsCsvRequest();
        csv.setFileName(convertFileName(scriptNode.getProps().get("filename")));
        csv.setIngoreFirstLine(Boolean.parseBoolean(scriptNode.getProps().get("ignoreFirstLine")));
        csv.setLineParams(convertCsvLines(scriptNode.getProps().get("variableNames")));
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

    private static void fillRequestBodyData(PtsApiBodyRequest bodyRequest, Map<String, String> propsMap) {
        if(propsMap.containsKey("Argument.value")) {
            bodyRequest.setRawData(propsMap.get("Argument.value"));
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
            bodyRequest.setForms(forms);
        }
    }

    private static void fillRequestHeaderData(PtsApiHeaderRequest headerRequest, List<ScriptNode> nodeList) {
        if(CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        List<KeyValueRequest> headers = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = nodeList.get(0).getProps().entrySet();
        for(Map.Entry<String, String> entry : entries) {
            KeyValueRequest header = new KeyValueRequest();
            header.setKey(entry.getKey());
            header.setValue(entry.getValue());
            headers.add(header);
        }
        headerRequest.setHeaders(headers);
    }

    private static void fillJsonProcessData(PtsApiReturnVarRequest returnVar, List<ScriptNode> jsonProcessList) {
        if(CollectionUtils.isEmpty(jsonProcessList)) {
            return;
        }
        List<ReturnVarRequest> varList = new ArrayList<>();
        for(ScriptNode jsonProcess : jsonProcessList) {
            ReturnVarRequest var = new ReturnVarRequest();
            var.setVarName(jsonProcess.getProps().get("JSONPostProcessor.referenceNames"));
            var.setParseExpress(jsonProcess.getProps().get("JSONPostProcessor.jsonPathExprs"));
            String index = jsonProcess.getProps().get("JSONPostProcessor.match_numbers");
            if(StringUtils.isNotBlank(index)) {
                var.setMatchIndex(Integer.parseInt(index));
            }
            var.setVarSource("JSON");
            varList.add(var);
        }
        returnVar.setVars(varList);
    }

    private static void fillJsonProcessData(PtsApiAssertRequest assertRequest, List<ScriptNode> assertList) {
        if(CollectionUtils.isEmpty(assertList)) {
            return;
        }
        List<AssertRequest> dataList = new ArrayList<>();
        for(ScriptNode scriptNode : assertList) {
            AssertRequest var = new AssertRequest();
            if("JSONPathAssertion".equals(scriptNode.getName())) {
                var.setCheckPointType("出参");
                var.setCheckObject(scriptNode.getProps().get("JSON_PATH"));
                var.setCheckCondition("8");
                var.setCheckContent(scriptNode.getProps().get("EXPECTED_VALUE"));
            } else if("ResponseAssertion".equals(scriptNode.getName())) {
                String testField = scriptNode.getProps().get("Assertion.test_field");
                if(testField.endsWith("response_data")) {
                    var.setCheckPointType("响应body");
                    var.setCheckObject("整个body");
                    String testType = scriptNode.getProps().get("Assertion.test_type");
                    if(!SUPPORT_CONDTION.contains(testType)) {
                        continue;
                    }
                    var.setCheckCondition(testType);
                    var.setCheckContent(findAssertValue(scriptNode.getProps()));
                } else {
                    continue;
                }
            }
            dataList.add(var);
        }
        assertRequest.setAsserts(dataList);
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
