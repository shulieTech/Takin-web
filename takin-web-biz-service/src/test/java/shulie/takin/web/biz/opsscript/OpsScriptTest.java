//package io.shulie.takin.web.app.opsscript;
//
//import com.alibaba.fastjson.JSON;
//
//import com.google.common.collect.Maps;
//import io.shulie.takin.common.beans.page.PagingList;
//import io.shulie.takin.web.app.Application;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashMap;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = Application.class)
//public class OpsScriptTest {
//
//    @Autowired
//    OpsScriptManageDAO opsScriptManageDAO;
//
//    @Autowired
//    OpsScriptFileDAO opsScriptFileDAO;
//
//    @Autowired
//    OpsScriptBatchNoDAO opsScriptBatchNoDAO;
//
//    @Autowired
//    OpsScriptExecuteResultDAO opsScriptExecuteResultDAO;
//
//    @Autowired
//    OpsScriptManageService opsScriptManageService;
//
//    @Autowired
//    TDictionaryDataMapper dictionaryDataMapper;
//
//    @Test
//    @Rollback
//    public void testAdd() {
//        OpsScriptManageEntity manageEntity = new OpsScriptManageEntity();
//        manageEntity.setScriptType(4);
//        manageEntity.setName("测试脚本");
//        manageEntity.setTenantId("1");
//        manageEntity.setUserId("1");
//        opsScriptManageDAO.save(manageEntity);
//
//        OpsScriptFileEntity fileEntity = new OpsScriptFileEntity();
//        fileEntity.setFileName("test");
//        fileEntity.setOpsScriptId(manageEntity.getId());
//        opsScriptFileDAO.save(fileEntity);
//
//        OpsScriptBatchNoEntity batchNoEntity = new OpsScriptBatchNoEntity();
//        batchNoEntity.setOpsScriptId(manageEntity.getId());
//        batchNoEntity.setBatchNo("001");
//        opsScriptBatchNoDAO.save(batchNoEntity);
//
//        OpsScriptExecuteResultEntity resultEntity = new OpsScriptExecuteResultEntity();
//        resultEntity.setExcutorId(1L);
//        resultEntity.setLogFilePath("/Users/");
//        resultEntity.setBatchId(batchNoEntity.getId());
//        resultEntity.setOpsScriptId(manageEntity.getId());
//        opsScriptExecuteResultDAO.save(resultEntity);
//    }
//
//    @Test
//    public void page() {
//        PagingList<OpsScriptVO> page =
//            opsScriptManageService.page(new OpsScriptParam());
//        System.out.println(JSON.toJSONString(page));
//
//        //        OpsScriptFileEntity one = opsScriptFileDAO.lambdaQuery().eq(OpsScriptFileEntity::getOpsScriptId, "1405087168039649281").eq(OpsScriptFileEntity::getFileType, 1).one();
//        //        System.out.println(JSON.toJSONString(one));
//    }
//
//    @Test
//    public void detail() {
//        OpsScriptParam param = new OpsScriptParam();
//        param.setId("1405087168039649281");
//        System.out.println(JSON.toJSONString(opsScriptManageService.detail(param)));
//        ;
//    }
//
//    @Test
//    public void del() {
//        OpsScriptParam param = new OpsScriptParam();
//        param.setId("1405087168039649281");
//        System.out.println(JSON.toJSONString(opsScriptManageService.delete(param)));
//    }
//
//    @Test
//    public void type() {
//        HashMap<String, Object> param = Maps.newHashMap();
//        param.put("valueActive", "Y");
//        param.put("typeAlias", "OPS_SCRIPT_TYPE");
//        System.out.println(JSON.toJSONString(dictionaryDataMapper.queryDictionaryList(param)));
//    }
//}
