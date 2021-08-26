//package io.shulie.takin.web.biz.service.script;
//
//import java.util.Collections;
//
//import cn.hutool.core.date.DateUtil;
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
//import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
//import io.shulie.takin.web.data.dao.script.ScriptDebugDAO;
//import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author liuchuan
// * @date 2021/5/17 5:09 下午
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class ScriptDebugServiceTest {
//
//    @Autowired
//    private ScriptDebugService scriptDebugService;
//
//    @Autowired
//    private SceneTaskService sceneTaskService;
//
//    @Autowired
//    private ScriptDebugDAO scriptDebugDAO;
//
//
//    @Test
//    public void testSave() {
//        ScriptDebugEntity scriptDebugEntity = new ScriptDebugEntity();
//        scriptDebugEntity.setScriptDeployId(1L);
//        scriptDebugEntity.setStatus(5);
//        scriptDebugEntity.setCustomerId(1L);
//        scriptDebugEntity.setUserId(1L);
//        scriptDebugEntity.setRequestNum(1);
//        scriptDebugEntity.setCreatedAt(DateUtil.date());
//        scriptDebugDAO.save(scriptDebugEntity);
//        System.out.println(scriptDebugEntity);
//    }
//
//    @Test
//    public void testCheck() {
//        sceneTaskService.checkBusinessActivitiesConfig(Collections.singletonList(103L));
//    }
//
//    @Test
//    public void testLeak() {
//        ScriptDebugEntity scriptDebugEntity = new ScriptDebugEntity();
//        scriptDebugEntity.setStatus(3);
//        scriptDebugEntity.setCloudReportId(1099L);
//        //scriptDebugService.checkLeak(scriptDebugEntity, "103", "1");
//    }
//
//}
