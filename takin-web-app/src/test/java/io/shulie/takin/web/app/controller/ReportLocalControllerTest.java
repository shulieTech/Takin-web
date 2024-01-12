package io.shulie.takin.web.app.controller;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.dto.report.ReportMockDTO;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.entrypoint.controller.report.ReportLocalController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ReportLocalControllerTest {

    @Autowired
    private ReportLocalController reportLocalController;

    @Test
    public void testMockList() {
        Response<List<ReportMockDTO>> response = reportLocalController.getMockList(37995L, 0, 2);
        System.out.println(JSON.toJSONString(response));
    }
}
