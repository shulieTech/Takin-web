package io.shulie.takin.web.biz.service.leakverify;

import java.util.List;

import io.shulie.takin.web.biz.convert.leakverify.LeakVerifyDeployOutputConverter;
import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployDetailOutput;
import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployOutput;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyDeployDAO;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployDetailResult;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shulie
 */
@Component
public class LeakVerifyDeployServiceImpl implements LeakVerifyDeployService {

    @Autowired
    private LeakVerifyDeployDAO leakVerifyDeployDAO;

    @Override
    public List<LeakVerifyDeployOutput> listLeakVerifyDeploy(Long leakVerifyId) {
        List<LeakVerifyDeployResult> leakVerifyDeployResults = leakVerifyDeployDAO.listLeakVerifyDeploy(leakVerifyId,
            false);
        return LeakVerifyDeployOutputConverter.INSTANCE.ofListLeakVerifyDeployOutput(leakVerifyDeployResults);
    }

    @Override
    public List<LeakVerifyDeployDetailOutput> queryLeakVerifyDeployDetail(Long leakVerifyDeployId) {
        List<LeakVerifyDeployDetailResult> leakVerifyDeployDetailResults = leakVerifyDeployDAO
            .queryLeakVerifyDeployDetail(leakVerifyDeployId, null, null, null);
        return LeakVerifyDeployOutputConverter.INSTANCE.ofListLeakVerifyDeployDetailOutput(
            leakVerifyDeployDetailResults);
    }

}
