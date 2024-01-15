package io.shulie.takin.web.app.service;

import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDeployService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class ScriptDebugServiceTest {

    @Autowired
    ScriptDebugService scriptDebugService;
    @Autowired
    private ScriptDeployService scriptDeployService;

    @Test
    public void test(){
//        ScriptDebugDoDebugRequest request = new ScriptDebugDoDebugRequest();
//        request.setScriptDeployId(17201L);
//        request.setRequestNum(1);
//        request.setConcurrencyNum(1);
//        scriptDebugService.debug(request);
        scriptDeployService.checkLeakFile(17201L, PressureSceneEnum.FLOW_DEBUG);
    }
}
