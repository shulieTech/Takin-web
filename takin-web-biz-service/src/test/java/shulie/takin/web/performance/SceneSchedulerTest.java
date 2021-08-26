//package shulie.takin.web.performance;
//
//import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
//import io.shulie.takin.channel.router.zk.ZkClient;
//import io.shulie.takin.web.biz.pojo.request.scenemanage.SceneSchedulerTaskCreateRequest;
//import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
//import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
//import io.shulie.takin.web.common.constant.RemoteConstant;
//import io.shulie.takin.web.common.http.HttpWebClient;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpMethod;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author mubai
// * @date 2020-12-03 14:31
// */
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class SceneSchedulerTest {
//
//    @Autowired
//    private SceneSchedulerTaskService sceneSchedulerTaskService;
//
//    @Autowired
//    private SceneTaskService  sceneTaskService;
//
//    @Autowired
//    private ZkClient zkClient;
//
//
//    @Autowired
//    private HttpWebClient httpWebClient;
//
//    @Test
//    public void testAdd(){
//
//        SceneSchedulerTaskCreateRequest request = new SceneSchedulerTaskCreateRequest();
//        request.setSceneId(100L);
//        request.setUserId(1L);
//        //request.setExecuteTime("2020-12-11 11:11");
//
//        sceneSchedulerTaskService.insert(request) ;
//    }
//
//
//
//    @Test
//    public void testDel(){
//
//        sceneSchedulerTaskService.deleteBySceneId(25L);
//    }
//
//    @Test
//    public void testZk(){
//        String path = "/takin/config" ;
//        zkClient.addNode(path,"cjc");
//    }
//
//    @Test
//    public void testStartScene(){
//
//        SceneActionParam param = new SceneActionParam();
//        //param.setUid(1L);
//        param.setSceneId(127L);
//        //param.setUid(1l);
//        param.setRequestUrl(RemoteConstant.SCENE_TASK_START_URL);
//        param.setHttpMethod(HttpMethod.POST);
//        httpWebClient.request(param);
//
//    }
//
//}
