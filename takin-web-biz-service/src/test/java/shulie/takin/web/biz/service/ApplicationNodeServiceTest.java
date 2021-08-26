//package io.shulie.takin.web.biz.service;
//
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
//import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author loseself
// * @date 2021/3/29 4:34 下午
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class ApplicationNodeServiceTest {
//
//    @Autowired
//    private ApplicationNodeService applicationNodeService;
//
//    @Test
//    public void testOperateProbe() {
//        ApplicationNodeOperateProbeRequest request = new ApplicationNodeOperateProbeRequest();
//        request.setApplicationId(6806475515940048896L);
//        request.setAgentId("123");
//        request.setOperateType(2);
//        request.setProbeId(1L);
//        applicationNodeService.operateProbe(request);
//    }
//
//
//
//}
