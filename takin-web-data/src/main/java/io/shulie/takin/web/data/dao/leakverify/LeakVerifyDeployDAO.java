package io.shulie.takin.web.data.dao.leakverify;

import java.util.List;

import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployDetailResult;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployResult;

/**
 * @author 漏数实例
 */
@Deprecated
public interface LeakVerifyDeployDAO {
    /**
     * 查询漏数实例
     * @param leakVerifyId
     * @param isCurrent
     * @return
     */
    List<LeakVerifyDeployResult> listLeakVerifyDeploy(Long leakVerifyId, boolean isCurrent);

    /**
     * 获取漏数验证实例详情
     * @param leakVerifyDeployId
     * @param leakVerifyId
     * @param applicationName
     * @param entryName
     * @return
     */
    List<LeakVerifyDeployDetailResult> queryLeakVerifyDeployDetail(Long leakVerifyDeployId, Long leakVerifyId, String applicationName, String entryName);

}
