package io.shulie.takin.web.app.service;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.response.linkmanage.ScriptNodeParsedResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class SceneSericeTest {
    @Autowired
    private SceneService sceneService;
    @Test
    public void testParseScriptNod() {
        String jsonString = "[{\"children\":[{\"children\":[{\"csvSet\":[],\"driverSet\":[],\"identification\":\"JMETER|Java请求1|6\",\"md5\":\"960f1d7f92a2e449c4cc9d6e1027c0fc\",\"name\":\"JavaSampler\",\"props\":{\"classname\":\"com.demo.test.JavaRequestTest\",\"test_aa\":\"aaa\",\"my_param\":\"java-request-test\"},\"requestPath\":\"JMETER|Java请求1\",\"samplerType\":\"JMETER\",\"testName\":\"Java请求1\",\"type\":\"SAMPLER\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/hashTree[2]/JavaSampler\",\"xpathMd5\":\"409af9484ef74cc7be67561281207c5e\"},{\"csvSet\":[],\"driverSet\":[],\"identification\":\"JMETER|JDBCRequest|6\",\"md5\":\"60cbc647e2aff51b6453e7b47a8a7509\",\"name\":\"JDBCSampler\",\"props\":{\"queryTimeout\":\"\",\"resultSetHandler\":\"Store as String\",\"resultSetMaxRows\":\"\",\"queryArguments\":\"\",\"query\":\"select * from t_user limit 10\",\"variableNames\":\"\",\"resultVariable\":\"result\",\"queryArgumentsTypes\":\"\",\"dataSource\":\"MyMysql\",\"queryType\":\"Select Statement\"},\"requestPath\":\"JMETER|JDBCRequest\",\"samplerType\":\"JMETER\",\"testName\":\"JDBC Request\",\"type\":\"SAMPLER\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/hashTree[2]/JDBCSampler\",\"xpathMd5\":\"08e8dd35acffd81cdbc1af24c8c1be1d\"},{\"csvSet\":[],\"driverSet\":[],\"identification\":\"JMETER|BeanShell取样器-1|6\",\"md5\":\"9051724920d410fdb5687453ca7de46b\",\"name\":\"BeanShellSampler\",\"props\":{\"BeanShellSampler.resetInterpreter\":\"false\",\"BeanShellSampler.parameters\":\"\",\"BeanShellSampler.filename\":\"\",\"BeanShellSampler.query\":\"vars.put(\\\"aaa\\\",\\\"aaa\\\")\"},\"requestPath\":\"JMETER|BeanShell取样器-1\",\"samplerType\":\"JMETER\",\"testName\":\"BeanShell 取样器-1\",\"type\":\"SAMPLER\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/hashTree[2]/BeanShellSampler\",\"xpathMd5\":\"9ffecf00e051f39ac700abbf345b4b94\"},{\"csvSet\":[],\"driverSet\":[],\"identification\":\"JMETER|JSR223Sampler|6\",\"md5\":\"065a034c6f9fd3d4b377cd1e34c3f343\",\"name\":\"JSR223Sampler\",\"props\":{\"filename\":\"\",\"cacheKey\":\"true\",\"scriptLanguage\":\"groovy\",\"parameters\":\"\",\"script\":\"vars.put(\\\"aab\\\",\\\"aab\\\")\"},\"requestPath\":\"JMETER|JSR223Sampler\",\"samplerType\":\"JMETER\",\"testName\":\"JSR223 Sampler\",\"type\":\"SAMPLER\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/hashTree[2]/JSR223Sampler\",\"xpathMd5\":\"06ffbdb39700ebac5a27531e9268fc4f\"},{\"csvSet\":[],\"driverSet\":[],\"md5\":\"0476ce9215791157e5126918f9c5a4ea\",\"name\":\"DebugSampler\",\"props\":{\"displaySystemProperties\":\"false\",\"displayJMeterProperties\":\"false\",\"displayJMeterVariables\":\"true\"},\"samplerType\":\"UNKNOWN\",\"testName\":\"调试取样器\",\"type\":\"SAMPLER\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/hashTree[2]/DebugSampler\",\"xpathMd5\":\"387529a19e196a4983619441a6629463\"}],\"csvSet\":[],\"driverSet\":[],\"md5\":\"c9bc21b7765b3b7d4ca538dab61e7d21\",\"name\":\"ThreadGroup\",\"props\":{\"ThreadGroup.on_sample_error\":\"continue\",\"ThreadGroup.scheduler\":\"false\",\"ThreadGroup.num_threads\":\"1\",\"ThreadGroup.same_user_on_next_iteration\":\"true\",\"ThreadGroup.ramp_time\":\"1\",\"ThreadGroup.delay\":\"\",\"ThreadGroup.duration\":\"\"},\"testName\":\"线程组\",\"type\":\"THREAD_GROUP\",\"xpath\":\"/jmeterTestPlan/hashTree/hashTree/ThreadGroup\",\"xpathMd5\":\"7dae7383a28b5c45069b528a454d1164\"}],\"csvSet\":[],\"driverSet\":[\"com.mysql.jdbc.Driver\"],\"md5\":\"f8ec78f20ab61ff54316a26b8784ea76\",\"name\":\"TestPlan\",\"testName\":\"JarRequest\",\"type\":\"TEST_PLAN\",\"xpath\":\"/jmeterTestPlan/hashTree/TestPlan\",\"xpathMd5\":\"0f1a197a2040e645dcdb4dfff8a3f960\"}]\n";
        List<ScriptNode> scriptNodes = JsonHelper.json2List(jsonString, ScriptNode.class);
        ScriptNodeParsedResponse response = sceneService.parseScriptNode(scriptNodes);
        System.out.println(JSON.toJSONString(response));
    }
}
