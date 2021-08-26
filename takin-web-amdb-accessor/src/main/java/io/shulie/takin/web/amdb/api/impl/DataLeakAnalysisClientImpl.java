package io.shulie.takin.web.amdb.api.impl;

import java.util.List;

import io.shulie.takin.web.amdb.api.DataLeakAnalysisClient;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDTO;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-14
 */
@Component
@Slf4j
public class DataLeakAnalysisClientImpl implements DataLeakAnalysisClient {

    @Override
    public List<LeakVerifyDeployDTO> getDataLeakLists(Long verifyId) {
        return null;
    }

    @Override
    public List<LeakVerifyDeployDetailDTO> getDataLeakDetails(Long verifyId, String applicationName, String entryName) {
        return null;
    }
}
