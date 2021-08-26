package io.shulie.takin.web.biz.service;

import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;

/**
 * @author fanxx
 * @date 2021/1/6 5:37 下午
 */
public interface VerifyTaskReportService {
    LeakVerifyTaskResultResponse getVerifyTaskReport(LeakVerifyTaskReportQueryRequest queryRequest);
}
