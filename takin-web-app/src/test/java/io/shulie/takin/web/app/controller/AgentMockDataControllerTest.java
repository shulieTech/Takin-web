package io.shulie.takin.web.app.controller;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.dto.report.ReportMockDTO;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.agent.AgentMockDataRequest;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.entrypoint.controller.agent.AgentPushController;
import io.shulie.takin.web.entrypoint.controller.report.ReportLocalController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class AgentMockDataControllerTest {

    @Autowired
    private AgentPushController agentPushController;

    @Test
    public void test01() {
        List<AgentMockDataRequest> requestList = new ArrayList<>();
        AgentMockDataRequest request01 = new AgentMockDataRequest();
        request01.setReportId(38174L);
        request01.setAppName("demo1");
        request01.setAgentId("127.0.0.1-8081");
        request01.setService("com.AClass");
        request01.setMethod("methodB");
        request01.setTotalCount(100L);
        request01.setSuccessCount(98L);
        request01.setFailCount(2L);
        request01.setTotalRt(20000000L);
        request01.setStartTime(1639700000000L);
        request01.setEndTime(1639800000000L);
        AgentMockDataRequest request02 = new AgentMockDataRequest();
        request02.setReportId(38174L);
        request02.setAppName("demo1");
        request02.setAgentId("127.0.0.2-8081");
        request02.setService("com.AClass");
        request02.setMethod("methodC");
        request02.setTotalCount(108L);
        request02.setSuccessCount(100L);
        request02.setFailCount(10L);
        request02.setTotalRt(28000000L);
        request02.setStartTime(1639700000000L);
        request02.setEndTime(1649800000000L);
        requestList.add(request01);
        requestList.add(request02);
        agentPushController.uploadMockData(requestList);
    }
}
