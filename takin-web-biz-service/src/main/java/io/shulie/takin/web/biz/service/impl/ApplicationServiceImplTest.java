package io.shulie.takin.web.biz.service.impl;

import java.util.Map;

import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.entity.ExceptionInfo;
import io.shulie.takin.web.biz.service.ApplicationService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fanxx
 * @date 2020/10/27 11:02 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ApplicationServiceImplTest.class)
public class ApplicationServiceImplTest extends TestCase {

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void testUploadAccessStatus() {
        NodeUploadDataDTO param = new NodeUploadDataDTO();
        param.setAgentId("123");
        param.setApplicationName("test-demo-1028");
        param.setNodeKey("456");
        Map<String, Object> errorMap = Maps.newHashMap();
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setErrorCode("001");
        exceptionInfo.setMessage("数据库异常");
        exceptionInfo.setDetail("url:eroor");
        errorMap.put("agent异常", exceptionInfo);
        param.setSwitchErrorMap(errorMap);
        applicationService.uploadAccessStatus(param);
    }

    @Test
    public void testGetApplicationInfo() {
        applicationService.getApplicationInfo("6727065154824966144");
    }
}
