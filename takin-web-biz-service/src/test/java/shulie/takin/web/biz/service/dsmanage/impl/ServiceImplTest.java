//package io.shulie.takin.web.biz.service.dsmanage.impl;
//
//import io.shulie.takin.web.biz.pojo.request.application.ApplicationDsCreateRequest;
//import io.shulie.takin.web.biz.pojo.request.application.ApplicationDsUpdateRequest;
//import io.shulie.takin.web.biz.service.dsManage.impl.ShadowKafkaServiceImpl;
//import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
//import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// * @author 无涯
//// * @date 2021/4/22 2:37 下午
// */
//public class ServiceImplTest {
//    @Test
//    public void KafkaJsonTest() {
//        ShadowKafkaServiceImpl shadowKafkaService = new ShadowKafkaServiceImpl();
//        ApplicationDsCreateRequest createRequest = new ApplicationDsCreateRequest();
//        createRequest.setConfig("[\n"
//            + "            {\n"
//            + "                \"key\":\"PT_业务主题\",\n"
//            + "                \"topic\":\"PT_业务主题\",\n"
//            + "                \"topicTokens\":\"PT_业务主题:影子主题token\",\n"
//            + "                \"group\":\"\",\n"
//            + "                \"systemIdToken\":\"\"\n"
//            + "            }\n"
//            + "        ]");
//        ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();
//        shadowKafkaService.addParserConfig(createRequest,createParam);
//        Assert.assertEquals("PT_业务主题",createParam.getUrl());
//    }
//
//    @Test
//    public void KafkaJsonUpdateTest() {
//        ShadowKafkaServiceImpl shadowKafkaService = new ShadowKafkaServiceImpl();
//        ApplicationDsUpdateRequest createRequest = new ApplicationDsUpdateRequest();
//        createRequest.setConfig("{\"key\": \"PT_业务主题\",\"topic\": \"PT_业务主题\",\"topicTokens\": \"PT_业务主题:影子主题token\",\"group\": \"\","
//            + "\"systemIdToken\": \"\"}");
//        ApplicationDsUpdateParam createParam = new ApplicationDsUpdateParam();
//        shadowKafkaService.updateParserConfig(createRequest,createParam);
//        Assert.assertEquals("PT_业务主题",createParam.getUrl());
//    }
//}
