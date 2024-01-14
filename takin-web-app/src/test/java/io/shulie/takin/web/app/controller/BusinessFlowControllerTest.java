package io.shulie.takin.web.app.controller;

import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareListRequest;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowAutoMatchRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.script.FileTypeEnum;
import io.shulie.takin.web.entrypoint.controller.agent.AgentApplicationController;
import io.shulie.takin.web.entrypoint.controller.businessflow.BusinessFlowController;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class BusinessFlowControllerTest {

    @Autowired
    private BusinessFlowController businessFlowController;

    @Test
    public void parseScriptAndSave() {
        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(1L, "c55cf771-12b5-49e4-a566-c84723a5f6f3",
            "test", "default", ContextSourceEnum.AGENT.getCode()));
        BusinessFlowParseRequest businessFlowParseRequest = new BusinessFlowParseRequest();
        FileManageUpdateRequest fileManageUpdateRequest = new FileManageUpdateRequest();
        fileManageUpdateRequest.setUploadId("111");
        fileManageUpdateRequest.setFileName("guanning.jmx");
        fileManageUpdateRequest.setIsDeleted(0);
        fileManageUpdateRequest.setFileType(FileTypeEnum.SCRIPT.getCode());
        businessFlowParseRequest.setScriptFile(fileManageUpdateRequest);
        businessFlowController.parseScriptAndSave(businessFlowParseRequest);

    }

    @Test
    public void autoMatchActivity() {
        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(1L, "c55cf771-12b5-49e4-a566-c84723a5f6f3",
            "test", "default", ContextSourceEnum.AGENT.getCode()));
        BusinessFlowAutoMatchRequest matchRequest = new BusinessFlowAutoMatchRequest();
        matchRequest.setId(1040L);
        businessFlowController.autoMatchActivity(matchRequest);
    }

    @Test
    public void matchActivity() {
        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(1L, "c55cf771-12b5-49e4-a566-c84723a5f6f3",
                "test", "default", ContextSourceEnum.AGENT.getCode()));
        SceneLinkRelateRequest sceneLinkRelateRequest = new SceneLinkRelateRequest();
        sceneLinkRelateRequest.setBusinessFlowId(1038L);
        sceneLinkRelateRequest.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        sceneLinkRelateRequest.setIdentification("JMETER|Java请求1|6");
        sceneLinkRelateRequest.setTestName("Java请求1");
        sceneLinkRelateRequest.setActivityName("测试计划_2024-01-10 11:41:57");
        sceneLinkRelateRequest.setXpathMd5("8e338c2cc7578155f05489c2bc786192");
        sceneLinkRelateRequest.setSamplerType(SamplerTypeEnum.JMETER);
        businessFlowController.matchActivity(sceneLinkRelateRequest);
    }
}
