package io.shulie.takin.cloud.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonAssert;
import io.shulie.takin.cloud.common.script.jmeter.ScriptJsonProcessor;
import io.shulie.takin.cloud.common.script.jmeter.ScriptKeyValueParam;
import io.shulie.takin.cloud.common.script.jmeter.ScriptResponseAssert;
import io.shulie.takin.cloud.model.script.ScriptData;
import io.shulie.takin.cloud.model.script.ScriptHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * ClassName:    JmeterScriptUtil
 * Package:    io.shulie.takin.cloud.app.util
 * Description:
 * Datetime:    2022/5/24   15:48
 *
 * @author chenhongqiao@shulie.com
 */
@Slf4j
public class PtsJmxBuildUtil {
    private PtsJmxBuildUtil() {}

    private static final String LESS_THAN = "&lt;";
    private static final String LESS_THAN_REPLACEMENT = "SHULIE_LESS_THAN_FLAG";
    private static final String GREATER_THAN = "&gt;";
    private static final String GREATER_THAN_REPLACEMENT = "SHULIE_GREATER_THAN_FLAG";
    private static final String AND = "&amp;";
    private static final String AND_REPLACEMENT = "SHULIE_AND_FLAG";
    private static final String APOS = "&apos;";
    private static final String APOS_REPLACEMENT = "SHULIE_APOS_FLAG";
    private static final String QUOTE = "&quot;";
    public static final String QUOTE_REPLACEMENT = "SHULIE_GUOTE_FLAG";

    public static final String HASH_TREE = "hashTree";
    public static final String GUI_CLASS = "guiclass";
    public static final String TEST_NAME = "testname";
    public static final String TEST_CLASS = "testclass";
    public static final String ENABLED = "enabled";
    public static final String STRING_PROP = "stringProp";

    public static final String INT_PROP = "intProp";
    public static final String BOOL_PROP = "boolProp";
    public static final String ELEMENT_PROP = "elementProp";
    public static final String ELEMENT_TYPE = "elementType";
    public static final String COLLECTION_PROP = "collectionProp";
    public static final String ARGUMENTS = "Arguments";
    public static final String NAME = "name";

    public static Element buildBase(Document element) {
        Element plan = element.addElement("jmeterTestPlan");
        plan.addAttribute("version", "1.2");
        plan.addAttribute("properties", "5.0");
        plan.addAttribute("jmeter", "5.4.1");
        return plan.addElement(HASH_TREE);
    }

    public static Element buildTestPlan(Element element, String testName) {
        Element plan = element.addElement("TestPlan");
        plan.addAttribute(GUI_CLASS, "TestPlanGui");
        plan.addAttribute(TEST_CLASS, "TestPlan");
        plan.addAttribute(TEST_NAME, testName);
        plan.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element stringProp = plan.addElement(STRING_PROP);
        stringProp.addAttribute(NAME, "TestPlan.comments");

        Element boolProp = plan.addElement(BOOL_PROP);
        boolProp.addAttribute(NAME, "TestPlan.functional_mode");
        boolProp.setText(Boolean.FALSE.toString());

        Element boolProp2 = plan.addElement(BOOL_PROP);
        boolProp2.addAttribute(NAME, "TestPlan.tearDown_on_shutdown");
        boolProp2.setText(Boolean.TRUE.toString());

        Element boolProp3 = plan.addElement(BOOL_PROP);
        boolProp3.addAttribute(NAME, "TestPlan.serialize_threadgroups");
        boolProp3.setText(Boolean.FALSE.toString());

        Element elementProp = plan.addElement(ELEMENT_PROP);
        elementProp.addAttribute(NAME, "TestPlan.user_defined_variables");
        elementProp.addAttribute(ELEMENT_TYPE, ARGUMENTS);
        elementProp.addAttribute(GUI_CLASS, "ArgumentsPanel");
        elementProp.addAttribute(TEST_CLASS, ARGUMENTS);
        elementProp.addAttribute(TEST_NAME, "用户定义的变量");
        elementProp.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collectionProp = elementProp.addElement(COLLECTION_PROP);
        collectionProp.addAttribute(NAME, "Arguments.arguments");

        Element stringProp2 = plan.addElement(STRING_PROP);
        stringProp2.addAttribute(NAME, "TestPlan.user_define_classpath");

        return element.addElement(HASH_TREE);
    }

    public static Element buildThreadGroup(Element element, String threadGroupName) {
        Element threadGroup = element.addElement("ThreadGroup");
        threadGroup.addAttribute(GUI_CLASS, "ThreadGroupGui");
        threadGroup.addAttribute(TEST_CLASS, "ThreadGroup");
        threadGroup.addAttribute(TEST_NAME, threadGroupName);
        threadGroup.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element stringProp = threadGroup.addElement(STRING_PROP);
        stringProp.addAttribute(NAME, "ThreadGroup.on_sample_error");
        stringProp.setText("continue");

        Element elementProp = threadGroup.addElement(ELEMENT_PROP);
        elementProp.addAttribute(NAME, "ThreadGroup.main_controller");
        elementProp.addAttribute(ELEMENT_TYPE, "LoopController");
        elementProp.addAttribute(GUI_CLASS, "LoopControlPanel");
        elementProp.addAttribute(TEST_CLASS, "LoopController");
        elementProp.addAttribute(TEST_NAME, "循环控制器");
        elementProp.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element loopBoolProp = elementProp.addElement(BOOL_PROP);
        loopBoolProp.addAttribute(NAME, "LoopController.continue_forever");
        loopBoolProp.setText(Boolean.FALSE.toString());

        Element loopStringProp = elementProp.addElement(STRING_PROP);
        loopStringProp.addAttribute(NAME, "LoopController.loops");
        loopStringProp.setText("1");

        Element stringProp2 = threadGroup.addElement(STRING_PROP);
        stringProp2.addAttribute(NAME, "ThreadGroup.num_threads");
        stringProp2.setText("1");

        Element stringProp3 = threadGroup.addElement(STRING_PROP);
        stringProp3.addAttribute(NAME, "ThreadGroup.ramp_time");
        stringProp3.setText("1");

        Element boolProp = threadGroup.addElement(BOOL_PROP);
        boolProp.addAttribute(NAME, "ThreadGroup.scheduler");
        boolProp.setText(Boolean.FALSE.toString());

        Element stringProp4 = threadGroup.addElement(STRING_PROP);
        stringProp4.addAttribute(NAME, "ThreadGroup.duration");
        stringProp4.setText("");

        Element stringProp5 = threadGroup.addElement(STRING_PROP);
        stringProp5.addAttribute(NAME, "ThreadGroup.delay");
        stringProp5.setText("");

        Element boolProp2 = threadGroup.addElement(BOOL_PROP);
        boolProp2.addAttribute(NAME, "ThreadGroup.same_user_on_next_iteration");
        boolProp2.setText(Boolean.TRUE.toString());

        return element.addElement(HASH_TREE);
    }

    public static Element buildTimer(Element element, String delay) {
        if(StringUtils.isBlank(delay)) {
            return element;
        }
        Element postProcessor = element.addElement("ConstantTimer");
        postProcessor.addAttribute(GUI_CLASS, "ConstantTimerGui");
        postProcessor.addAttribute(TEST_CLASS, "ConstantTimer");
        postProcessor.addAttribute(TEST_NAME, "固定定时器");
        postProcessor.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element delayE = postProcessor.addElement(STRING_PROP);
        delayE.addAttribute(NAME, "ConstantTimer.delay");
        delayE.setText(delay);

        return element.addElement(HASH_TREE);
    }

    public static Element buildHttpDefault(Element element, JSONObject dataMap) {
        if (CollUtil.isEmpty(dataMap)) {
            return element;
        }
        Element header = element.addElement("ConfigTestElement");
        header.addAttribute(GUI_CLASS, "HttpDefaultsGui");
        header.addAttribute(TEST_CLASS, "ConfigTestElement");
        header.addAttribute(TEST_NAME, "HTTP请求默认值");
        header.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element elementProp = header.addElement(ELEMENT_PROP);
        elementProp.addAttribute(NAME, "HTTPsampler.Arguments");
        elementProp.addAttribute(ELEMENT_TYPE, "Arguments");
        elementProp.addAttribute(GUI_CLASS, "HTTPArgumentsPanel");
        elementProp.addAttribute(TEST_CLASS, "Arguments");
        elementProp.addAttribute(TEST_NAME, "用户定义的变量");
        elementProp.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collectionProp = elementProp.addElement(COLLECTION_PROP);
        collectionProp.addAttribute(NAME, "Arguments.arguments");

        Element domain = header.addElement(STRING_PROP);
        domain.addAttribute(NAME, "HTTPSampler.domain");
        domain.setText(dataMap.getString("domain"));

        Element port = header.addElement(STRING_PROP);
        port.addAttribute(NAME, "HTTPSampler.port");
        port.setText(dataMap.getString("port"));

        Element protocol = header.addElement(STRING_PROP);
        protocol.addAttribute(NAME, "HTTPSampler.protocol");
        protocol.setText(dataMap.getString("protocol"));

        Element contentEncoding = header.addElement(STRING_PROP);
        contentEncoding.addAttribute(NAME, "HTTPSampler.contentEncoding");
        contentEncoding.setText(dataMap.getString("contentEncoding"));

        Element path = header.addElement(STRING_PROP);
        path.addAttribute(NAME, "HTTPSampler.path");
        path.setText(dataMap.getString("path"));

        Element concurrentPool = header.addElement(STRING_PROP);
        concurrentPool.addAttribute(NAME, "HTTPSampler.concurrentPool");
        concurrentPool.setText("6");

        Element connect_timeout = header.addElement(STRING_PROP);
        connect_timeout.addAttribute(NAME, "HTTPSampler.connect_timeout");
        connect_timeout.setText("");

        Element response_timeout = header.addElement(STRING_PROP);
        response_timeout.addAttribute(NAME, "HTTPSampler.response_timeout");
        response_timeout.setText("");

        return element.addElement(HASH_TREE);
    }

    public static Element buildCounter(Element element, List<JSONObject> jsonList) {
        if (CollUtil.isEmpty(jsonList)) {
            return element;
        }
        for(JSONObject jsonObject : jsonList) {
            Element header = element.addElement("CounterConfig");
            header.addAttribute(GUI_CLASS, "CounterConfigGui");
            header.addAttribute(TEST_CLASS, "CounterConfig");
            header.addAttribute(TEST_NAME, "计数器");
            header.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element start = header.addElement(STRING_PROP);
            start.addAttribute(NAME, "CounterConfig.start");
            start.setText(jsonObject.getString("start"));

            Element end = header.addElement(STRING_PROP);
            end.addAttribute(NAME, "CounterConfig.end");
            end.setText(jsonObject.getString("end"));

            Element incr = header.addElement(STRING_PROP);
            incr.addAttribute(NAME, "CounterConfig.incr");
            incr.setText(jsonObject.getString("incr"));

            Element name = header.addElement(STRING_PROP);
            name.addAttribute(NAME, "CounterConfig.name");
            name.setText(jsonObject.getString("name"));

            Element format = header.addElement(STRING_PROP);
            format.addAttribute(NAME, "CounterConfig.format");
            format.setText(jsonObject.getString("format"));

            Element per_user = header.addElement(BOOL_PROP);
            per_user.addAttribute(NAME, "CounterConfig.per_user");
            per_user.setText("false");

            element.addElement(HASH_TREE);
        }
        return element;
    }

    public static Element buildHttpHeader(Element element, List<ScriptHeader> headers) {
        if (CollUtil.isEmpty(headers)) {
            return element;
        }
        Element header = element.addElement("HeaderManager");
        header.addAttribute(GUI_CLASS, "HeaderPanel");
        header.addAttribute(TEST_CLASS, "HeaderManager");
        header.addAttribute(TEST_NAME, "HTTP信息头管理器");
        header.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collectionProp = header.addElement(COLLECTION_PROP);
        collectionProp.addAttribute(NAME, "HeaderManager.headers");

        Set<String> headerName = new HashSet<>();
        for (ScriptHeader scriptHeader : headers) {
            String key = scriptHeader.getKey();
            if(StringUtils.isBlank(key) || headerName.contains(key)) {
                continue;
            }
            headerName.add(key);
            Element elementProp = collectionProp.addElement(ELEMENT_PROP);
            elementProp.addAttribute(NAME, "");
            elementProp.addAttribute(ELEMENT_TYPE, "Header");
            Element stringPropKey = elementProp.addElement(STRING_PROP);
            stringPropKey.addAttribute(NAME, "Header.name");
            stringPropKey.setText(key);

            Element stringPropVal = elementProp.addElement(STRING_PROP);
            stringPropVal.addAttribute(NAME, "Header.value");
            stringPropVal.setText(scriptHeader.getValue());
        }
        return element.addElement(HASH_TREE);
    }

    public static Element buildApiBeanshellPre(Element element, List<String> scripts) {
        if (CollUtil.isEmpty(scripts)) {
            return element;
        }
        for(String scriptContent : scripts) {
            if(StringUtils.isBlank(scriptContent)) {
                continue;
            }
            Element header = element.addElement("BeanShellPreProcessor");
            header.addAttribute(GUI_CLASS, "TestBeanGUI");
            header.addAttribute(TEST_CLASS, "BeanShellPreProcessor");
            header.addAttribute(TEST_NAME, "BeanShell预处理程序");
            header.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element filename = header.addElement(STRING_PROP);
            filename.addAttribute(NAME, "filename");
            filename.setText("");

            Element parameters = header.addElement(STRING_PROP);
            parameters.addAttribute(NAME, "parameters");
            parameters.setText("");

            Element resetInterpreter = header.addElement(BOOL_PROP);
            resetInterpreter.addAttribute(NAME, "resetInterpreter");
            resetInterpreter.setText("false");

            Element script = header.addElement(STRING_PROP);
            script.addAttribute(NAME, "script");
            script.setText(scriptContent);

            element.addElement(HASH_TREE);
        }
        return element;
    }

    public static Element buildJsonPostProcessor(Element element, List<ScriptJsonProcessor> processors) {
        if(CollUtil.isEmpty(processors)) {
            return element;
        }
        for (ScriptJsonProcessor processor : processors) {
            if(StringUtils.isBlank(processor.getReferenceNames())
                    || StringUtils.isBlank(processor.getJsonPathExprs())) {
                continue;
            }
            Element postProcessor = element.addElement("JSONPostProcessor");
            postProcessor.addAttribute(GUI_CLASS, "JSONPostProcessorGui");
            postProcessor.addAttribute(TEST_CLASS, "JSONPostProcessor");
            postProcessor.addAttribute(TEST_NAME, "JSON提取器");
            postProcessor.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element referenceNamesKey = postProcessor.addElement(STRING_PROP);
            referenceNamesKey.addAttribute(NAME, "JSONPostProcessor.referenceNames");
            referenceNamesKey.setText(processor.getReferenceNames());

            Element jsonPathExprsKey = postProcessor.addElement(STRING_PROP);
            jsonPathExprsKey.addAttribute(NAME, "JSONPostProcessor.jsonPathExprs");
            jsonPathExprsKey.setText(processor.getJsonPathExprs());

            Element matchNumbersKey = postProcessor.addElement(STRING_PROP);
            matchNumbersKey.addAttribute(NAME, "JSONPostProcessor.match_numbers");
            if(processor.getMatchNumbers() != null) {
                matchNumbersKey.setText(processor.getMatchNumbers());
            }

            element.addElement(HASH_TREE);
        }
        return element;
    }

    public static Element buildRegexExtractor(Element element, List<ScriptJsonProcessor> regexExtractors) {
        if(CollUtil.isEmpty(regexExtractors)) {
            return element;
        }
        for (ScriptJsonProcessor regex : regexExtractors) {
            if(StringUtils.isBlank(regex.getUseHeader())
                    || StringUtils.isBlank(regex.getReferenceNames())
                    || StringUtils.isBlank(regex.getJsonPathExprs())) {
                continue;
            }
            Element postProcessor = element.addElement("RegexExtractor");
            postProcessor.addAttribute(GUI_CLASS, "RegexExtractorGui");
            postProcessor.addAttribute(TEST_CLASS, "RegexExtractor");
            postProcessor.addAttribute(TEST_NAME, "正则表达式提取器");
            postProcessor.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element useHeaders = postProcessor.addElement(STRING_PROP);
            useHeaders.addAttribute(NAME, "RegexExtractor.useHeaders");
            useHeaders.setText(regex.getUseHeader());

            Element refName = postProcessor.addElement(STRING_PROP);
            refName.addAttribute(NAME, "RegexExtractor.refname");
            refName.setText(regex.getReferenceNames());

            Element regexE = postProcessor.addElement(STRING_PROP);
            regexE.addAttribute(NAME, "RegexExtractor.regex");
            regexE.setText(regex.getJsonPathExprs());

            Element template = postProcessor.addElement(STRING_PROP);
            template.addAttribute(NAME, "RegexExtractor.template");

            Element defaultE = postProcessor.addElement(STRING_PROP);
            defaultE.addAttribute(NAME, "RegexExtractor.default");

            Element matchNumbersKey = postProcessor.addElement(STRING_PROP);
            matchNumbersKey.addAttribute(NAME, "RegexExtractor.match_number");
            if(regex.getMatchNumbers() != null) {
                matchNumbersKey.setText(regex.getMatchNumbers());
            }
            element.addElement(HASH_TREE);
        }
        return element;
    }

    public static Element buildResponseAssert(Element element, List<ScriptResponseAssert> asserts) {
        if(CollUtil.isEmpty(asserts)) {
            return element;
        }
        for (ScriptResponseAssert responseAssert : asserts) {
            if(StringUtils.isBlank(responseAssert.getTestType())
                    || StringUtils.isBlank(responseAssert.getTestStrings())
                    || StringUtils.isBlank(responseAssert.getTestField())) {
                continue;
            }
            Element postProcessor = element.addElement("ResponseAssertion");
            postProcessor.addAttribute(GUI_CLASS, "AssertionGui");
            postProcessor.addAttribute(TEST_CLASS, "ResponseAssertion");
            postProcessor.addAttribute(TEST_NAME, "响应断言");
            postProcessor.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element collectionKey = postProcessor.addElement(COLLECTION_PROP);
            collectionKey.addAttribute(NAME, "Asserion.test_strings");
            Element testStringsKey = collectionKey.addElement(STRING_PROP);
            testStringsKey.addAttribute(NAME, String.valueOf(new Random().nextInt(5000) + 1000));
            testStringsKey.setText(responseAssert.getTestStrings());

            Element customMessageKey = postProcessor.addElement(STRING_PROP);
            customMessageKey.addAttribute(NAME, "Assertion.custom_message");

            Element testFieldKey = postProcessor.addElement(STRING_PROP);
            testFieldKey.addAttribute(NAME, "Assertion.test_field");
            testFieldKey.setText(responseAssert.getTestField());

            Element assumeSuccessKey = postProcessor.addElement(BOOL_PROP);
            assumeSuccessKey.addAttribute(NAME, "Assertion.assume_success");
            assumeSuccessKey.setText(Boolean.FALSE.toString());

            Element testTypeKey = postProcessor.addElement(INT_PROP);
            testTypeKey.addAttribute(NAME, "Assertion.test_type");
            testTypeKey.setText(responseAssert.getTestType());

            element.addElement(HASH_TREE);
        }
        return element;
    }

    public static Element buildJsonAssert(Element element, List<ScriptJsonAssert> asserts) {
        if(CollUtil.isEmpty(asserts)) {
            return element;
        }
        for (ScriptJsonAssert jsonAssert : asserts) {
            if(StringUtils.isBlank(jsonAssert.getJsonPath())
                    || StringUtils.isBlank(jsonAssert.getExpectedValue())
                    || jsonAssert.getJsonvalidation() == null) {
                continue;
            }
            Element postProcessor = element.addElement("JSONPathAssertion");
            postProcessor.addAttribute(GUI_CLASS, "JSONPathAssertionGui");
            postProcessor.addAttribute(TEST_CLASS, "JSONPathAssertion");
            postProcessor.addAttribute(TEST_NAME, "JSON断言");
            postProcessor.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element jsonPathKey = postProcessor.addElement(STRING_PROP);
            jsonPathKey.addAttribute(NAME, "JSON_PATH");
            jsonPathKey.setText(jsonAssert.getJsonPath());

            Element expectedValueKey = postProcessor.addElement(STRING_PROP);
            expectedValueKey.addAttribute(NAME, "EXPECTED_VALUE");
            expectedValueKey.setText(jsonAssert.getExpectedValue());

            Element jsonvalidationKey = postProcessor.addElement(BOOL_PROP);
            jsonvalidationKey.addAttribute(NAME, "JSONVALIDATION");
            jsonvalidationKey.setText(jsonAssert.getJsonvalidation().toString());

            Element expectNullKey = postProcessor.addElement(BOOL_PROP);
            expectNullKey.addAttribute(NAME, "EXPECT_NULL");
            expectNullKey.setText(Boolean.FALSE.toString());

            Element invertKey = postProcessor.addElement(BOOL_PROP);
            invertKey.addAttribute(NAME, "INVERT");
            invertKey.setText(Boolean.FALSE.toString());

            Element isregexKey = postProcessor.addElement(BOOL_PROP);
            isregexKey.addAttribute(NAME, "ISREGEX");
            isregexKey.setText(Boolean.FALSE.toString());

            element.addElement(HASH_TREE);

        }
        return element;
    }

    public static Element buildCsvData(Element element, List<ScriptData> datas) {
        if (CollUtil.isEmpty(datas)) {
            return element;
        }
        for (ScriptData scriptData : datas) {
            if(StringUtils.isBlank(scriptData.getPath())
                    || StringUtils.isBlank(scriptData.getFormat())
                    || scriptData.getIgnoreFirstLine() == null) {
                continue;
            }
            Element data = element.addElement("CSVDataSet");
            data.addAttribute(GUI_CLASS, "TestBeanGUI");
            data.addAttribute(TEST_CLASS, "CSVDataSet");
            data.addAttribute(TEST_NAME, "CSV数据文件设置");
            data.addAttribute(ENABLED, Boolean.TRUE.toString());

            Element stringProp = data.addElement(STRING_PROP);
            stringProp.addAttribute(NAME, "filename");
            stringProp.setText(scriptData.getPath());

            Element fileEncoding = data.addElement(STRING_PROP);
            fileEncoding.addAttribute(NAME, "fileEncoding");
            fileEncoding.setText("UTF-8");

            Element variableNames = data.addElement(STRING_PROP);
            variableNames.addAttribute(NAME, "variableNames");
            variableNames.setText(scriptData.getFormat());

            Element ignoreFirstLine = data.addElement(BOOL_PROP);
            ignoreFirstLine.addAttribute(NAME, "ignoreFirstLine");
            ignoreFirstLine.setText(Objects.isNull(scriptData.getIgnoreFirstLine()) ? Boolean.FALSE.toString() : scriptData.getIgnoreFirstLine().toString());

            Element delimiter = data.addElement(STRING_PROP);
            delimiter.addAttribute(NAME, "delimiter");
            delimiter.setText(",");

            Element quotedData = data.addElement(BOOL_PROP);
            quotedData.addAttribute(NAME, "quotedData");
            quotedData.setText(Boolean.FALSE.toString());

            Element recycle = data.addElement(BOOL_PROP);
            recycle.addAttribute(NAME, "recycle");
            recycle.setText(Boolean.TRUE.toString());

            Element stopThread = data.addElement(BOOL_PROP);
            stopThread.addAttribute(NAME, "stopThread");
            stopThread.setText(Boolean.FALSE.toString());

            Element shareMode = data.addElement(STRING_PROP);
            shareMode.addAttribute(NAME, "shareMode");
            shareMode.setText("shareMode.all");

            element.addElement(HASH_TREE);
        }

        return element;
    }

    public static Element buildHttpSampler(Element element, String apiName, URL queryUrl, String method, String getData, String postData, Map<String, String> paramMap) {
        Element sampler = element.addElement("HTTPSamplerProxy");
        sampler.addAttribute(GUI_CLASS, "HttpTestSampleGui");
        sampler.addAttribute(TEST_CLASS, "HTTPSamplerProxy");
        sampler.addAttribute(TEST_NAME, StringUtils.isNotBlank(apiName) ? apiName : "HTTP请求");
        sampler.addAttribute(ENABLED, Boolean.TRUE.toString());

        if(StringUtils.isNotBlank(getData)) {
            List<ScriptKeyValueParam> params = JSON.parseArray(getData, ScriptKeyValueParam.class);
            Element arguments = sampler.addElement(ELEMENT_PROP);
            arguments.addAttribute(NAME, "HTTPsampler.Arguments");
            arguments.addAttribute(ELEMENT_TYPE, ARGUMENTS);
            arguments.addAttribute(GUI_CLASS, "HTTPArgumentsPanel");
            arguments.addAttribute(TEST_CLASS, ARGUMENTS);
            arguments.addAttribute(TEST_NAME, "User Defined Variables");
            arguments.addAttribute(ENABLED, Boolean.TRUE.toString());
            Element collection = arguments.addElement(COLLECTION_PROP);
            collection.addAttribute(NAME, "Arguments.arguments");
            if(CollectionUtils.isNotEmpty(params)) {
                for(ScriptKeyValueParam param : params) {
                    Element arg = collection.addElement(ELEMENT_PROP);
                    arg.addAttribute("name", param.getKey());
                    arg.addAttribute("elementType", "HTTPArgument");
                    Element alwaysEncodeKey = arg.addElement(BOOL_PROP);
                    alwaysEncodeKey.addAttribute(NAME, "HTTPArgument.always_encode");
                    alwaysEncodeKey.setText(Boolean.TRUE.toString());
                    Element valueKey = arg.addElement(STRING_PROP);
                    valueKey.addAttribute(NAME, "Argument.value");
                    valueKey.setText(param.getValue());
                    Element metaKey = arg.addElement(STRING_PROP);
                    metaKey.addAttribute(NAME, "Argument.metadata");
                    metaKey.setText("=");
                    Element useEqualsKey = arg.addElement(BOOL_PROP);
                    useEqualsKey.addAttribute(NAME, "HTTPArgument.use_equals");
                    useEqualsKey.setText(Boolean.TRUE.toString());
                    Element nameKey = arg.addElement(STRING_PROP);
                    nameKey.addAttribute(NAME, "Argument.name");
                    nameKey.setText(param.getKey());
                }
            }
        }

        if (CharSequenceUtil.isNotBlank(postData)) {
            Element postBodyRaw = sampler.addElement(BOOL_PROP);
            postBodyRaw.addAttribute(NAME, "HTTPSampler.postBodyRaw");
            postBodyRaw.setText(Boolean.TRUE.toString());

            Element arguments = sampler.addElement(ELEMENT_PROP);
            arguments.addAttribute(NAME, "HTTPsampler.Arguments");
            arguments.addAttribute(ELEMENT_TYPE, ARGUMENTS);

            Element collectionProp = arguments.addElement(COLLECTION_PROP);
            collectionProp.addAttribute(NAME, "Arguments.arguments");

            Element elementProp = collectionProp.addElement(ELEMENT_PROP);
            elementProp.addAttribute(NAME, "");
            elementProp.addAttribute(ELEMENT_TYPE, "HTTPArgument");

            Element boolProp = elementProp.addElement(BOOL_PROP);
            boolProp.addAttribute(NAME, "HTTPArgument.always_encode");
            boolProp.setText(Boolean.FALSE.toString());

            Element stringProp = elementProp.addElement(STRING_PROP);
            stringProp.addAttribute(NAME, "Argument.value");
            stringProp.setText(postData);

            Element stringProp2 = elementProp.addElement(STRING_PROP);
            stringProp2.addAttribute(NAME, "Argument.metadata");
            stringProp2.setText("=");
        }

        Element domain = sampler.addElement(STRING_PROP);
        domain.addAttribute(NAME, "HTTPSampler.domain");
        domain.setText(queryUrl.getHost());

        Element port = sampler.addElement(STRING_PROP);
        port.addAttribute(NAME, "HTTPSampler.port");
        port.setText(queryUrl.getPort() != -1 ? String.valueOf(queryUrl.getPort()) : "");

        Element protocol = sampler.addElement(STRING_PROP);
        protocol.addAttribute(NAME, "HTTPSampler.protocol");
        protocol.setText(queryUrl.getProtocol());

        Element contentEncoding = sampler.addElement(STRING_PROP);
        contentEncoding.addAttribute(NAME, "HTTPSampler.contentEncoding");
        contentEncoding.setText("UTF-8");

        Element path = sampler.addElement(STRING_PROP);
        path.addAttribute(NAME, "HTTPSampler.path");
        StringBuilder pathQuery = new StringBuilder(queryUrl.getPath());
        if (CharSequenceUtil.isNotBlank(queryUrl.getQuery())) {
            pathQuery.append("?").append(queryUrl.getQuery());
        }
        path.setText(pathQuery.toString());

        Element methodE = sampler.addElement(STRING_PROP);
        methodE.addAttribute(NAME, "HTTPSampler.method");
        methodE.setText(method);

        Element followRedirects = sampler.addElement(BOOL_PROP);
        followRedirects.addAttribute(NAME, "HTTPSampler.follow_redirects");
        followRedirects.setText(paramMap.get("allowForward"));

        Element autoRedirects = sampler.addElement(BOOL_PROP);
        autoRedirects.addAttribute(NAME, "HTTPSampler.auto_redirects");
        autoRedirects.setText(Boolean.FALSE.toString());

        Element useKeepalive = sampler.addElement(BOOL_PROP);
        useKeepalive.addAttribute(NAME, "HTTPSampler.use_keepalive");
        useKeepalive.setText(paramMap.get("keepAlive"));

        Element doMultipartPost = sampler.addElement(BOOL_PROP);
        doMultipartPost.addAttribute(NAME, "HTTPSampler.DO_MULTIPART_POST");
        doMultipartPost.setText(Boolean.FALSE.toString());

        Element embeddedUrlRe = sampler.addElement(STRING_PROP);
        embeddedUrlRe.addAttribute(NAME, "HTTPSampler.embedded_url_re");
        embeddedUrlRe.setText("");

        Element connectTimeout = sampler.addElement(STRING_PROP);
        connectTimeout.addAttribute(NAME, "HTTPSampler.connect_timeout");
        connectTimeout.setText("");

        Element responseTimeout = sampler.addElement(STRING_PROP);
        responseTimeout.addAttribute(NAME, "HTTPSampler.response_timeout");
        responseTimeout.setText(paramMap.get("requestTimeout"));

        return element.addElement(HASH_TREE);
    }

    public static Element buildJavaSampler(Element element, String apiName, String className, Map<String, String> propsMap) {
        Element data = element.addElement("JavaSampler");
        data.addAttribute(GUI_CLASS, "JavaTestSamplerGui");
        data.addAttribute(TEST_CLASS, "JavaSampler");
        data.addAttribute(TEST_NAME, apiName);
        data.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element arg = data.addElement(ELEMENT_PROP);
        arg.addAttribute(NAME, "arguments");
        arg.addAttribute(ELEMENT_TYPE, "Arguments");
        arg.addAttribute(GUI_CLASS, "ArgumentsPanel");
        arg.addAttribute(TEST_CLASS, "Arguments");
        arg.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element collection = arg.addElement(COLLECTION_PROP);
        collection.addAttribute(NAME, "Arguments.arguments");

        propsMap.forEach((key, value) -> {
            if(!"classname".equals(key)) {
                Element param = collection.addElement(ELEMENT_PROP);
                param.addAttribute(NAME, key);
                param.addAttribute(ELEMENT_TYPE, "Argument");

                Element argName = param.addElement(STRING_PROP);
                argName.addAttribute(NAME, "Argument.name");
                argName.setText(key);

                Element argValue = param.addElement(STRING_PROP);
                argValue.addAttribute(NAME, "Argument.value");
                argValue.setText(value);

                Element argMetadata = param.addElement(STRING_PROP);
                argMetadata.addAttribute(NAME, "Argument.metadata");
                argMetadata.setText("=");
            }
        });
        Element classNameE = data.addElement(STRING_PROP);
        classNameE.addAttribute(NAME, "classname");
        classNameE.setText(className);
        return element.addElement(HASH_TREE);
    }

    public static Element buildViewResultsTree(Element element) {
        Element data = element.addElement("ResultCollector");
        data.addAttribute(GUI_CLASS, "ViewResultsFullVisualizer");
        data.addAttribute(TEST_CLASS, "ResultCollector");
        data.addAttribute(TEST_NAME, "View Results Tree");
        data.addAttribute(ENABLED, Boolean.TRUE.toString());

        Element errorLogging = data.addElement(BOOL_PROP);
        errorLogging.addAttribute(NAME, "ResultCollector.error_logging");
        errorLogging.setText(Boolean.FALSE.toString());

        Element objProp = data.addElement("objProp");
        Element name = objProp.addElement("name");
        name.setText("saveConfig");
        Element value = objProp.addElement("value");
        value.addAttribute("class", "SampleSaveConfiguration");
        Element time = value.addElement("time");
        time.setText(Boolean.TRUE.toString());
        Element latency = value.addElement("latency");
        latency.setText(Boolean.FALSE.toString());
        Element timestamp = value.addElement("timestamp");
        timestamp.setText(Boolean.TRUE.toString());
        Element success = value.addElement("success");
        success.setText(Boolean.TRUE.toString());
        Element label = value.addElement("label");
        label.setText(Boolean.TRUE.toString());
        Element code = value.addElement("code");
        code.setText(Boolean.TRUE.toString());
        Element message = value.addElement("message");
        message.setText(Boolean.TRUE.toString());
        Element threadName = value.addElement("threadName");
        threadName.setText(Boolean.FALSE.toString());
        Element dataType = value.addElement("dataType");
        dataType.setText(Boolean.FALSE.toString());
        Element encoding = value.addElement("encoding");
        encoding.setText(Boolean.FALSE.toString());
        Element assertions = value.addElement("assertions");
        assertions.setText(Boolean.TRUE.toString());
        Element subresults = value.addElement("subresults");
        subresults.setText(Boolean.FALSE.toString());
        Element responseData = value.addElement("responseData");
        responseData.setText(Boolean.TRUE.toString());
        Element samplerData = value.addElement("samplerData");
        samplerData.setText(Boolean.TRUE.toString());
        Element xml = value.addElement("xml");
        xml.setText(Boolean.TRUE.toString());
        Element fieldNames = value.addElement("fieldNames");
        fieldNames.setText(Boolean.FALSE.toString());
        Element responseHeaders = value.addElement("responseHeaders");
        responseHeaders.setText(Boolean.TRUE.toString());
        Element requestHeaders = value.addElement("requestHeaders");
        requestHeaders.setText(Boolean.TRUE.toString());
        Element responseDataOnError = value.addElement("responseDataOnError");
        responseDataOnError.setText(Boolean.FALSE.toString());
        Element saveAssertionResultsFailureMessage = value.addElement("saveAssertionResultsFailureMessage");
        saveAssertionResultsFailureMessage.setText(Boolean.FALSE.toString());
        Element assertionsResultsToSave = value.addElement("assertionsResultsToSave");
        assertionsResultsToSave.setText("0");
        Element url = value.addElement("url");
        url.setText(Boolean.TRUE.toString());

        Element filename = data.addElement(STRING_PROP);
        filename.addAttribute(NAME, "filename");
        filename.setText("./result.xml");

        return element.addElement(HASH_TREE);
    }

    public static String getDocumentStr(Document document) {
        try (StringWriter stringWriter = new StringWriter()) {
            document.write(stringWriter);
            String finalStr = stringWriter.getString();
            finalStr = specialCharRepAfter(finalStr);
            return finalStr;
        } catch (IOException e) {
            log.warn("JmeterScriptUtil#getDocumentStr", e);
            throw new IORuntimeException(e);
        }
    }

    public static String specialCharRepAfter(String content) {
        return content.replace(LESS_THAN_REPLACEMENT, LESS_THAN)
            .replace(GREATER_THAN_REPLACEMENT, GREATER_THAN)
            .replace(AND_REPLACEMENT, AND)
            .replace(APOS_REPLACEMENT, APOS)
            .replace(QUOTE_REPLACEMENT, QUOTE);
    }
}
