//package io.shulie.takin.web.param;
//
//import java.util.List;
//import java.util.Random;
//
//import com.alibaba.fastjson.JSON;
//
//import com.google.common.collect.Lists;
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.entrypoint.controller.perfomanceanaly.PerformanceBaseDataController;
//import io.shulie.takin.web.biz.pojo.perfomanceanaly.PerformanceBaseDataReq;
//import io.shulie.takin.web.common.vo.perfomanceanaly.PerformanceThreadDataVO;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @ClassName PerformanceBaseDataTest
// * @author qianshui
// * @date 2020/11/10 上午10:39
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class PerformanceBaseDataTest {
//
//    @Autowired
//    private PerformanceBaseDataController performanceBaseDataController;
//
//    @Test
//    public void testadd() {
//        long beginTime = 1604937720000L;
//        List<PerformanceBaseDataReq> reqs = Lists.newArrayList();
//        List<String> statusList = Lists.newArrayList("RUNNING", "BLOCK", "WAIT", "STARTED");
//        List<Double> userRate = Lists.newArrayList(0.11d, 0.12d, 0.13d, 0.14d);
//        for(int i = 0; i < 100; i++) {
//            PerformanceBaseDataReq req = new PerformanceBaseDataReq();
//            req.setAgentId("agent_id_001");
//            req.setAppName("demo_test");
//            req.setAppIp("127.0.0.1");
//            req.setProcessId(110L);
//            req.setProcessName("demo_test.jar");
//            req.setTimestamp(beginTime);
//            //req.setTotalMemory(8000);
//            //req.setPermMemory(1000);
//            //req.setYoungMemory(1200);
//            //req.setOldMemory(3000d);
//            req.setYoungGcCount(100);
//            req.setFullGcCount(1);
//            req.setYoungGcCost(3000L);
//            req.setFullGcCost(100L);
//            List<PerformanceThreadDataVO> threadDataList = Lists.newArrayList();
//            Random random = new Random();
//            int size = random.nextInt(10);
//            size = size == 0 ? 1 : size;
//            for(int j = 0; j < size; j++) {
//                PerformanceThreadDataVO threadData = new PerformanceThreadDataVO();
//                threadData.setThreadName("my-thread-" + (j + 1));
//                Random temp = new Random();
//                int index = temp.nextInt(3);
//                threadData.setThreadStatus(statusList.get(index));
//                threadData.setThreadCpuUsage(userRate.get(index));
//                threadData.setThreadStack("org.apache.tomcat"
//                    + " io.shulie.pamirs");
//                threadDataList.add(threadData);
//            }
//            req.setThreadDataList(threadDataList);
//            beginTime += 1000;
//            reqs.add(req);
//            performanceBaseDataController.receivePerformanceBaseData(req);
//        }
//        System.out.println(JSON.toJSONString(reqs));
//    }
//}
