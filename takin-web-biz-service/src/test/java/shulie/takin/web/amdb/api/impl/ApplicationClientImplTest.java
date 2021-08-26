//package io.shulie.takin.web.amdb.api.impl;
//
//import java.util.List;
//
//import com.google.common.collect.Lists;
//import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
//import io.shulie.takin.web.amdb.api.ApplicationClient;
//import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
//import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
//import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
//import io.shulie.takin.web.app.Application;
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
// * @date 2020/10/19 10:07 上午
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = Application.class)
//public class ApplicationClientImplTest extends TestCase {
//
//    @Autowired
//    ApplicationClient applicationClient;
//
//    @Autowired
//    private ApplicationEntranceClient applicationEntranceClient;
//
//    @Test
//    public void testPageApplications() {
//        ApplicationQueryDTO queryDTO = new ApplicationQueryDTO();
//        queryDTO.setAppNames(Lists.newArrayList("1"));
//        queryDTO.setCurrentPage(0);
//        queryDTO.setPageSize(1);
//        System.out.println(applicationClient.pageApplications(queryDTO));
//    }
//
//    @Test
//    public void testPageApplicationNodes() {
//        ApplicationNodeQueryDTO queryDTO = new ApplicationNodeQueryDTO();
//        queryDTO.setAppNames("demo-fire-app");
//        queryDTO.setCurrentPage(0);
//        queryDTO.setPageSize(1);
//        applicationClient.pageApplicationNodes(queryDTO);
//    }
//
//    @Test
//    public void testMqs() {
//        List<ServiceInfoDTO> mqTopicGroups = applicationEntranceClient.getMqTopicGroups("spring-kafka-24-consumer");
//        System.out.println();
//    }
//}
