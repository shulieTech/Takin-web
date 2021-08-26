//package io.shulie.takin.web.data.dao;
//
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.data.dao.application.ApplicationDsManageDAO;
//import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
///**
// * @author loseself
// * @date 2021/3/27 7:27 下午
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = Application.class)
//public class ApplicationDsManageDAOTest {
//
//    @Autowired
//    private ApplicationDsManageDAO applicationDsManageDao;
//
//    @Test
//    public void testListByApplicationId() {
//        List<ApplicationDsManageEntity> applicationDsManageEntities = applicationDsManageDao.listByApplicationId(1002L);
//        System.out.println(applicationDsManageEntities);
//    }
//}
