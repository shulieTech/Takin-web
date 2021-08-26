package io.shulie.takin.web.biz.service.leakverify;

import java.util.List;

import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployDetailOutput;
import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployOutput;

/**
 * @author zhaoyong
 */
public interface LeakVerifyDeployService {

    /**
     * 漏数实例列表
     *
     * @return
     */
    List<LeakVerifyDeployOutput> listLeakVerifyDeploy(Long leakVerifyId);

    /**
     * 获取漏数实例详情，如果回传leakVerifyDeployId，查询表数据，如果不回传，查询实时数据
     *
     * @return
     */
    List<LeakVerifyDeployDetailOutput> queryLeakVerifyDeployDetail(Long leakVerifyDeployId);
}
