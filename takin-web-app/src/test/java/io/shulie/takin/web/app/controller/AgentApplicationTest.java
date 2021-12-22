package io.shulie.takin.web.app.controller;

import java.util.Arrays;

import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareListRequest;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.entrypoint.controller.agent.AgentApplicationController;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class AgentApplicationTest {

    @Autowired
    private AgentApplicationController agentApplicationController;

    @Test
    public void testPushMiddlewareList() {
        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(25L, "c55cf771-12b5-49e4-a566-c84723a5f6f3",
            "prod", "aj", ContextSourceEnum.AGENT.getCode()));

        PushMiddlewareRequest pushMiddlewareRequest = new PushMiddlewareRequest();
        pushMiddlewareRequest.setApplicationName("alibaba-rocketmq-326");

        PushMiddlewareListRequest PushMiddlewareListRequest = new PushMiddlewareListRequest();
        PushMiddlewareListRequest.setArtifactId("fastjson");
        PushMiddlewareListRequest.setGroupId("com.alibaba");
        PushMiddlewareListRequest.setVersion("1.2.3");
        pushMiddlewareRequest.setMiddlewareList(Arrays.asList(PushMiddlewareListRequest));
        agentApplicationController.pushMiddlewareList(pushMiddlewareRequest);
        while (true) {

        }


    }


}
