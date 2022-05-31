package io.shulie.takin.web.biz.service.interfaceperformance;

import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailResponse;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 10:20 上午
 */
public interface PerformanceParamService {
    /**
     * 更新数据文件
     */
    void updatePerformanceData(PerformanceDataFileRequest request);

    /**
     * 获取参数详情
     */
    PerformanceParamDetailResponse detail(PerformanceParamDetailRequest request);

    /**
     * 读取文件内容详情
     */
    PerformanceParamDetailResponse fileContentDetail(PerformanceParamDetailRequest request);
}
