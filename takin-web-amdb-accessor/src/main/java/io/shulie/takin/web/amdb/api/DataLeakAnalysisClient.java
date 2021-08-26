package io.shulie.takin.web.amdb.api;

import java.util.List;

import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDTO;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDetailDTO;

/**
 * 漏数检测
 * @author shiyajian
 * create: 2020-12-14
 */
public interface DataLeakAnalysisClient {

    /**
     * 根据漏数id查询当前漏数列表
     * @param verifyId
     * @return
     */
    List<LeakVerifyDeployDTO> getDataLeakLists(Long verifyId);

    /**
     * 根据漏数id，应用名称，入口名称查询漏数实例详情
     * @param verifyId
     * @param applicationName
     * @param entryName
     * @return
     */
    List<LeakVerifyDeployDetailDTO> getDataLeakDetails(Long verifyId,String applicationName,String entryName);
}
