//package io.shulie.takin.web.amdb.api.impl;
//
//import java.util.Arrays;
//
//import io.shulie.takin.web.amdb.api.TraceClient;
//import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
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
// * @date 2020/10/28 7:31 下午
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = Application.class)
//public class TraceClientImplTest extends TestCase {
//
//    @Autowired
//    private TraceClient traceClient;
//
//    @Test
//    public void testPageApplications() {
//        TraceInfoQueryDTO query = new TraceInfoQueryDTO();
//        query.setEntranceList(Arrays.asList("/one/testmq#GET"));
//        query.setStartTime(1610000279656L);
//        traceClient.listEntryTraceInfo(query);
//    }
//
//    @Test
//    public void testPageApplications2() {
//        System.out.println(traceClient.getTraceDetailById("e364a8c016099155326311017d541b").toString());
//    }
//}
