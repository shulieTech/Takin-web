package io.shulie.takin.web.app.service;

import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.service.report.impl.SummaryService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class SummaryServiceTest {

    @Resource
    SummaryService summaryService;

    @Test
    public void test(){
        summaryService.calcReportSummay(38000L);
    }
}
