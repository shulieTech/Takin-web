package io.shulie.takin.web.biz.service.interfaceperformance.impl;

/**
 * @Author: vernon
 * @Date: 2022/5/25 16:46
 * @Description:
 */
public class TestConstant {

    public static String default_jmx_str ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<jmeterTestPlan version=\"1.2\" properties=\"5.0\" jmeter=\"5.4.1\">\n" +
            "  <hashTree>\n" +
            "    <TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"easydemo-3\" enabled=\"true\">\n" +
            "      <stringProp name=\"TestPlan.comments\"></stringProp>\n" +
            "      <boolProp name=\"TestPlan.functional_mode\">false</boolProp>\n" +
            "      <boolProp name=\"TestPlan.tearDown_on_shutdown\">true</boolProp>\n" +
            "      <boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>\n" +
            "      <elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">\n" +
            "        <collectionProp name=\"Arguments.arguments\">\n" +
            "          <elementProp name=\"api\" elementType=\"Argument\">\n" +
            "            <stringProp name=\"Argument.name\">api</stringProp>\n" +
            "            <stringProp name=\"Argument.value\">api</stringProp>\n" +
            "            <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
            "          </elementProp>\n" +
            "        </collectionProp>\n" +
            "      </elementProp>\n" +
            "      <stringProp name=\"TestPlan.user_define_classpath\"></stringProp>\n" +
            "    </TestPlan>\n" +
            "    <hashTree>\n" +
            "      <HeaderManager guiclass=\"HeaderPanel\" testclass=\"HeaderManager\" testname=\"HTTP Header Manager\" enabled=\"true\">\n" +
            "        <collectionProp name=\"HeaderManager.headers\">\n" +
            "          <elementProp name=\"\" elementType=\"Header\">\n" +
            "            <stringProp name=\"Header.name\">Content-Type</stringProp>\n" +
            "            <stringProp name=\"Header.value\">application/json</stringProp>\n" +
            "          </elementProp>\n" +
            "          <elementProp name=\"\" elementType=\"Header\">\n" +
            "            <stringProp name=\"Header.name\">User-Agent1</stringProp>\n" +
            "            <stringProp name=\"Header.value\">PerfomanceTest</stringProp>\n" +
            "          </elementProp>\n" +
            "        </collectionProp>\n" +
            "      </HeaderManager>\n" +
            "      <hashTree/>\n" +
            "      <ThreadGroup guiclass=\"ThreadGroupGui\" testclass=\"ThreadGroup\" testname=\"Thread Group\" enabled=\"true\">\n" +
            "        <stringProp name=\"ThreadGroup.on_sample_error\">continue</stringProp>\n" +
            "        <elementProp name=\"ThreadGroup.main_controller\" elementType=\"LoopController\" guiclass=\"LoopControlPanel\" testclass=\"LoopController\" testname=\"Loop Controller\" enabled=\"true\">\n" +
            "          <boolProp name=\"LoopController.continue_forever\">false</boolProp>\n" +
            "          <stringProp name=\"LoopController.loops\">1</stringProp>\n" +
            "        </elementProp>\n" +
            "        <stringProp name=\"ThreadGroup.num_threads\">1</stringProp>\n" +
            "        <stringProp name=\"ThreadGroup.ramp_time\">1</stringProp>\n" +
            "        <boolProp name=\"ThreadGroup.scheduler\">false</boolProp>\n" +
            "        <stringProp name=\"ThreadGroup.duration\"></stringProp>\n" +
            "        <stringProp name=\"ThreadGroup.delay\"></stringProp>\n" +
            "        <boolProp name=\"ThreadGroup.same_user_on_next_iteration\">true</boolProp>\n" +
            "      </ThreadGroup>\n" +
            "      <hashTree>\n" +
            "        <HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\"userRegister\" enabled=\"true\">\n" +
            "          <boolProp name=\"HTTPSampler.postBodyRaw\">true</boolProp>\n" +
            "          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\">\n" +
            "            <collectionProp name=\"Arguments.arguments\">\n" +
            "              <elementProp name=\"\" elementType=\"HTTPArgument\">\n" +
            "                <boolProp name=\"HTTPArgument.always_encode\">false</boolProp>\n" +
            "                <stringProp name=\"Argument.value\">{&quot;mobile&quot;:&quot;15558172211&quot;,&quot;password&quot;:&quot;123456&quot;,&quot;nickName&quot;:&quot;name-10000&quot;,&quot;email&quot;:&quot;10000000@qq.com&quot;,&quot;birthDay&quot;:&quot;2000-11-05&quot;,&quot;provinceName&quot;:&quot;浙江&quot;,&quot;cityName&quot;:&quot;杭州&quot;}&#xd;\n" +
            "</stringProp>\n" +
            "                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
            "              </elementProp>\n" +
            "            </collectionProp>\n" +
            "          </elementProp>\n" +
            "          <stringProp name=\"HTTPSampler.domain\">192.168.1.213</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.port\">28881</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.protocol\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.contentEncoding\">UTF-8</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.path\">/gateway/${api}/register</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.method\">POST</stringProp>\n" +
            "          <boolProp name=\"HTTPSampler.follow_redirects\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.auto_redirects\">false</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.DO_MULTIPART_POST\">false</boolProp>\n" +
            "          <stringProp name=\"HTTPSampler.embedded_url_re\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.response_timeout\"></stringProp>\n" +
            "        </HTTPSamplerProxy>\n" +
            "        <hashTree/>\n" +
            "        <HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\"userRegister2\" enabled=\"true\">\n" +
            "          <boolProp name=\"HTTPSampler.postBodyRaw\">true</boolProp>\n" +
            "          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\">\n" +
            "            <collectionProp name=\"Arguments.arguments\">\n" +
            "              <elementProp name=\"\" elementType=\"HTTPArgument\">\n" +
            "                <boolProp name=\"HTTPArgument.always_encode\">false</boolProp>\n" +
            "                <stringProp name=\"Argument.value\">{&quot;mobile&quot;:&quot;15558172211&quot;,&quot;password&quot;:&quot;123456&quot;,&quot;nickName&quot;:&quot;name-10000&quot;,&quot;email&quot;:&quot;10000000@qq.com&quot;,&quot;birthDay&quot;:&quot;2000-11-05&quot;,&quot;provinceName&quot;:&quot;浙江&quot;,&quot;cityName&quot;:&quot;杭州&quot;}&#xd;\n" +
            "</stringProp>\n" +
            "                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
            "              </elementProp>\n" +
            "            </collectionProp>\n" +
            "          </elementProp>\n" +
            "          <stringProp name=\"HTTPSampler.domain\">192.168.1.213</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.port\">28881</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.protocol\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.contentEncoding\">UTF-8</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.path\">/gateway/${api}/register</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.method\">POST</stringProp>\n" +
            "          <boolProp name=\"HTTPSampler.follow_redirects\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.auto_redirects\">false</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.DO_MULTIPART_POST\">false</boolProp>\n" +
            "          <stringProp name=\"HTTPSampler.embedded_url_re\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.response_timeout\"></stringProp>\n" +
            "        </HTTPSamplerProxy>\n" +
            "        <hashTree/>\n" +
            "        <HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\"userRegister3\" enabled=\"true\">\n" +
            "          <boolProp name=\"HTTPSampler.postBodyRaw\">true</boolProp>\n" +
            "          <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\">\n" +
            "            <collectionProp name=\"Arguments.arguments\">\n" +
            "              <elementProp name=\"\" elementType=\"HTTPArgument\">\n" +
            "                <boolProp name=\"HTTPArgument.always_encode\">false</boolProp>\n" +
            "                <stringProp name=\"Argument.value\">{&quot;mobile&quot;:&quot;15558172211&quot;,&quot;password&quot;:&quot;123456&quot;,&quot;nickName&quot;:&quot;name-10000&quot;,&quot;email&quot;:&quot;10000000@qq.com&quot;,&quot;birthDay&quot;:&quot;2000-11-05&quot;,&quot;provinceName&quot;:&quot;浙江&quot;,&quot;cityName&quot;:&quot;杭州&quot;}&#xd;\n" +
            "</stringProp>\n" +
            "                <stringProp name=\"Argument.metadata\">=</stringProp>\n" +
            "              </elementProp>\n" +
            "            </collectionProp>\n" +
            "          </elementProp>\n" +
            "          <stringProp name=\"HTTPSampler.domain\">192.168.1.213</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.port\">28881</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.protocol\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.contentEncoding\">UTF-8</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.path\">/gateway/${api}/register</stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.method\">POST</stringProp>\n" +
            "          <boolProp name=\"HTTPSampler.follow_redirects\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.auto_redirects\">false</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>\n" +
            "          <boolProp name=\"HTTPSampler.DO_MULTIPART_POST\">false</boolProp>\n" +
            "          <stringProp name=\"HTTPSampler.embedded_url_re\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>\n" +
            "          <stringProp name=\"HTTPSampler.response_timeout\"></stringProp>\n" +
            "        </HTTPSamplerProxy>\n" +
            "        <hashTree/>\n" +
            "      </hashTree>\n" +
            "      <ResultCollector guiclass=\"ViewResultsFullVisualizer\" testclass=\"ResultCollector\" testname=\"View Results Tree\" enabled=\"true\">\n" +
            "        <boolProp name=\"ResultCollector.error_logging\">false</boolProp>\n" +
            "        <objProp>\n" +
            "          <name>saveConfig</name>\n" +
            "          <value class=\"SampleSaveConfiguration\">\n" +
            "            <time>true</time>\n" +
            "            <latency>true</latency>\n" +
            "            <timestamp>true</timestamp>\n" +
            "            <success>true</success>\n" +
            "            <label>true</label>\n" +
            "            <code>true</code>\n" +
            "            <message>true</message>\n" +
            "            <threadName>true</threadName>\n" +
            "            <dataType>true</dataType>\n" +
            "            <encoding>false</encoding>\n" +
            "            <assertions>true</assertions>\n" +
            "            <subresults>true</subresults>\n" +
            "            <responseData>false</responseData>\n" +
            "            <samplerData>false</samplerData>\n" +
            "            <xml>false</xml>\n" +
            "            <fieldNames>true</fieldNames>\n" +
            "            <responseHeaders>false</responseHeaders>\n" +
            "            <requestHeaders>false</requestHeaders>\n" +
            "            <responseDataOnError>false</responseDataOnError>\n" +
            "            <saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage>\n" +
            "            <assertionsResultsToSave>0</assertionsResultsToSave>\n" +
            "            <bytes>true</bytes>\n" +
            "            <sentBytes>true</sentBytes>\n" +
            "            <url>true</url>\n" +
            "            <threadCounts>true</threadCounts>\n" +
            "            <idleTime>true</idleTime>\n" +
            "            <connectTime>true</connectTime>\n" +
            "          </value>\n" +
            "        </objProp>\n" +
            "        <stringProp name=\"filename\"></stringProp>\n" +
            "      </ResultCollector>\n" +
            "      <hashTree/>\n" +
            "    </hashTree>\n" +
            "  </hashTree>\n" +
            "</jmeterTestPlan>";
}
