package io.shulie.takin.web.app.service;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.util.ActivityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ScriptDebugServiceTest {

    @Autowired
    ScriptDebugService scriptDebugService;

    @Test
    public void test(){
        ScriptDebugDoDebugRequest request = new ScriptDebugDoDebugRequest();
        request.setScriptDeployId(17182L);
        request.setRequestNum(1);
        request.setConcurrencyNum(1);
        scriptDebugService.debug(request);
    }
}
