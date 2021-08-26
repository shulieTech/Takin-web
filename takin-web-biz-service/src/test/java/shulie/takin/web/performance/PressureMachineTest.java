//package io.shulie.takin.web.performance;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.alibaba.fastjson.JSON;
//
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineLogService;
//import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineService;
//import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
//import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineLogDao;
//import io.shulie.takin.web.data.dao.perfomanceanaly.PressureMachineStatisticsDao;
//import io.shulie.takin.web.data.mapper.mysql.PressureMachineMapper;
//import io.shulie.takin.web.data.model.mysql.PressureMachineEntity;
//import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
//import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
//import io.shulie.takin.web.entrypoint.controller.perfomanceanaly.PressureMachineController;
//import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineInsertRequest;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author mubai
// * @date 2020-11-13 09:25
// */
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class PressureMachineTest {
//
//    @Autowired
//    private PressureMachineController pressureMachineController;
//
//    @Autowired
//    private PressureMachineService pressureMachineService;
//
//    @Autowired
//    private PressureMachineStatisticsService pressureMachineStatisticsService;
//
//    @Autowired
//    private PressureMachineLogService machineLogService;
//
//    @Autowired
//    private PressureMachineLogDao pressureMachineLogDao;
//
//    @Autowired
//    private PressureMachineMapper pressureMachineMapper;
//
//    @Autowired
//    private PressureMachineStatisticsDao pressureMachineStatisticsDao;
//
//    public static void assembleInputData(PressureMachineInsertRequest input) {
//        input.setName("cjc-test2");
//        input.setIp("127.0.0.5");
//        input.setCpu(4);
//        input.setMemory(111111L);
//        input.setDisk(22222222L);
//
//        input.setCpuUsage(new BigDecimal(0.2));
//        input.setCpuLoad(new BigDecimal(0.3));
//        input.setMemoryUsed(new BigDecimal(0.8));
//        input.setDiskIoWait(new BigDecimal(0.2));
//        // input.setTransmittedUsage(new BigDecimal(0.5));
//        input.setTransmittedIn(10 * 1024 * 1024L);
//        input.setTransmittedIn(30 * 1024 * 1024L);
//        List<Long> sceneIds = new ArrayList<>();
//        sceneIds.add(2L);
//        sceneIds.add(3l);
//        input.setSceneId(sceneIds);
//        input.setStatus(0);
//
//    }
//
//    public static void main(String[] args) {
//        PressureMachineInsertRequest request = new PressureMachineInsertRequest();
//        assembleInputData(request);
//        System.out.println(JSON.toJSONString(request));
//    }
//
//    @Test
//    public void testUpload() {
//        PressureMachineInsertRequest input = new PressureMachineInsertRequest();
//        assembleInputData(input);
//        pressureMachineController.uploadMachineInfo(input);
//    }
//
//    @Test
//    public void testInsertStatistics() {
//
//        PressureMachineStatisticsInsertParam param = new PressureMachineStatisticsInsertParam();
//        param.setMachineTotal(10);
//        param.setMachineFree(3);
//        param.setMachineOffline(2);
//        param.setMachinePressured(5);
//        pressureMachineStatisticsService.insert(param);
//    }
//
//    @Test
//    public void testStatics() {
//        pressureMachineStatisticsService.statistics();
//    }
//
//    @Test
//    public void statistics() {
//        pressureMachineStatisticsService.statistics();
//    }
//
//    @Test
//    public void getMachineLog() {
//        PressureMachineLogQueryParam param = new PressureMachineLogQueryParam();
//        param.setMachineId(4L);
//        param.setStartTime("2020-11-01 11:11:11");
//        param.setEndTime("2020-11-22 11:11:11");
//        pressureMachineLogDao.queryList(param);
//    }
//
//    @Test
//    public void testGetByIP() {
//        PressureMachineEntity byIp = pressureMachineMapper.getByIp("127.0.0.2");
//        System.out.println(byIp);
//    }
//
//    @Test
//    public void testClearStatisc() {
//        pressureMachineStatisticsDao.clearRubbishData("2020-11-26 22:22:22");
//    }
//
//    @Test
//    public void testStatisInsert() {
//        PressureMachineStatisticsInsertParam param = new PressureMachineStatisticsInsertParam();
//        param.setMachineTotal(100);
//        param.setMachineOffline(20);
//        param.setMachinePressured(30);
//        param.setMachineFree(50);
//        pressureMachineStatisticsDao.insert(param);
//    }
//
//}
