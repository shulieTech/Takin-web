package io.shulie.takin.web.biz.service;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlCreateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDetailRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.SqlTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlBatchRefsResponse;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlRefsResponse;

/**
 * @author fanxx
 * @date 2020/12/31 3:23 下午
 */
public interface LeakSqlService {
    void createLeakCheckConfig(LeakSqlCreateRequest createRequest);

    void updateLeakCheckConfig(LeakSqlUpdateRequest updateRequest);

    void deleteLeakCheckConfig(LeakSqlDeleteRequest deleteRequest);

    LeakSqlRefsResponse getLeakCheckConfigDetail(LeakSqlDetailRequest detailRequest);

    List<LeakSqlBatchRefsResponse> getBatchLeakCheckConfig(LeakSqlBatchRefsRequest refsRequest);

    List<VerifyTaskConfig> getVerifyTaskConfig(LeakSqlBatchRefsRequest refsRequest);

    Boolean getSceneLeakConfig(Long sceneId);

    String testSqlConnection(SqlTestRequest sqlTestRequest);
}
