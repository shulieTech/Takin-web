//package io.shulie.takin.web.cloud;
//
//import java.util.Collections;
//import java.util.Date;
//
//import cn.hutool.core.date.DateUtil;
//import io.shulie.takin.cloud.common.pojo.dto.scenemanage.UploadFileDTO;
//import io.shulie.takin.cloud.open.req.scenemanage.CloudUpdateSceneFileRequest;
//import io.shulie.takin.common.beans.response.ResponseResult;
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author liuchuan
// * @date 2021/4/26 8:39 下午
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class SceneManageApiTest {
//
//    @Autowired
//    private SceneManageApi sceneManageApi;
//
//    @Test
//    public void testUpdateSceneFileByScriptId() {
//        CloudUpdateSceneFileRequest request = new CloudUpdateSceneFileRequest();
//        request.setScriptType(0);
//        UploadFileDTO uploadFileDTO = new UploadFileDTO();
//        uploadFileDTO.setUploadTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
//        uploadFileDTO.setId(1L);
//        uploadFileDTO.setFileName("测试文件");
//        uploadFileDTO.setUploadPath("~/test/tmp");
//        uploadFileDTO.setIsDeleted(0);
//        uploadFileDTO.setUploadedData(0L);
//        uploadFileDTO.setIsSplit(0);
//        uploadFileDTO.setFileType(0);
//        request.setUploadFiles(Collections.singletonList(uploadFileDTO));
//
//        ResponseResult<Object> responseResult = sceneManageApi.updateSceneFileByScriptId(request);
//        System.out.println(responseResult);
//    }
//
//}
