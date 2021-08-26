//package io.shulie.takin.web.biz.service.application.impl;
//
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.biz.pojo.request.application.ApplicationErrorQueryRequest;
//import io.shulie.takin.web.biz.service.application.ApplicationErrorService;
//import junit.framework.TestCase;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author fanxx
// * @date 2020/10/27 3:26 下午
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = Application.class)
//public class ApplicationErrorServiceImplTest extends TestCase {
//
//    @Autowired
//    ApplicationErrorService applicationErrorService;
//
//    @Test
//    public void test() {
//        ApplicationErrorQueryRequest queryRequest = new ApplicationErrorQueryRequest(6726765863975784448L);
//        applicationErrorService.list(queryRequest);
//    }
//}