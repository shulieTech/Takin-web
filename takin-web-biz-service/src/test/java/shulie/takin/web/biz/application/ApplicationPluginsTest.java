//package io.shulie.takin.web.app.application;
//
//import java.util.Date;
//import java.util.Map;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//import io.shulie.takin.cloud.common.utils.DateUtil;
//import io.shulie.takin.common.beans.page.PagingList;
//import io.shulie.takin.web.app.Application;
//import io.shulie.takin.web.ext.entity.UserExt;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.web.client.RestTemplate;
//
//@Slf4j
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = Application.class)
//public class ApplicationPluginsTest {
//    @Autowired
//    ApplicationPluginsConfigService configService;
//
//    @Autowired
//    RestTemplate restTemplate;
//
//    @Before
//    public void test() {
//        UserExt user = new UserExt();
//        user.setTenantId(1L);
//
//    }
//
//    @Test
//    public void testList() {
//        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
//        param.setApplicationId(101L);
//        param.setTenantId(1L);
//        PagingList<ApplicationPluginsConfigVO> listByParam = configService.getPageByParam(param);
//        log.info(JSON.toJSONString(listByParam));
//    }
//
//    @Test
//    public void testSave() {
//        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
//        param.setTenantId(1L);
//        param.setApplicationId(6800689288141869056L);
//        param.setApplicationName("testSave");
//        param.setUserId(1L);
//        param.setConfigItem("redis影子key有效期");
//        param.setConfigDesc("可自定义设置redis影子key有效期，默认与业务key有效期一致。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。");
//        param.setConfigValue("-1");
//        param.setConfigKey("redis_expire");
//        configService.add(param);
//    }
//
//    @Test
//    public void testUpdate() {
//        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
//        param.setId(1394840796766085121L);
//        param.setTenantId(1L);
//        param.setApplicationId(101L);
//        param.setApplicationName("测试testSave");
//        param.setUserId(1L);
//        param.setConfigItem("redis影子key有效期");
//        param.setConfigDesc("可自定义设置redis影子key有效期，默认与业务key有效期一致。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。");
//        param.setConfigValue("与业务key一致");
//        configService.update(param);
//    }
//
//    @Test
//    public void testjson() {
//        String now = DateUtil.getYYYYMMDDHHMMSS(new Date());
//        String content = "登录通知\n" +
//            "【用户名】： " + "caijy" + "\n" +
//            "【登录 IP】：127.0.0.1" + "\n" +
//            "【登录时间】：" + now + "\n" +
//            "【总登录次数】：11";
//        JSONObject inner = new JSONObject();
//        inner.put("content", content);
//        JSONObject req = new JSONObject();
//        req.put("msgtype", "text");
//        req.put("text", inner);
//        log.info(req.toJSONString());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(req, headers);
//        try {
//            log.info(DINGDING_URL);
//            log.info(pushEnable + "");
//            //            Map<String,Object> result = restTemplate.postForObject(DINGDING_URL, httpEntity, Map.class);
//            //            if (!result.get("errcode").equals(0)){
//            //                log.error("钉钉发送通知失败！原因："+result.get("errmsg"));
//            //            }
//        } catch (Exception e) {
//            log.error("发送钉钉通知失败！错误原因" + e.getMessage(), e);
//        }
//    }
//}
