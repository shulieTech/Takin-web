package io.shulie.takin.web.app.service;

import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.report.ReportMockRequest;
import io.shulie.takin.web.biz.service.report.ReportMockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ReportMockServiceTest {
    @Autowired
    private ReportMockService reportMockService;
    @Test
    public void testSaveReportMockData() {
        ReportMockRequest mockRequest = new ReportMockRequest();
        mockRequest.setReportId(37995L);
        mockRequest.setStartTime("2024-01-14 10:36:58");
        mockRequest.setEndTime("2024-01-14 12:38:58");
        mockRequest.setTenantId(6L);
        mockRequest.setEnvCode("test");
        reportMockService.saveReportMockData(mockRequest);
    }
}
