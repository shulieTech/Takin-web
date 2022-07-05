package io.shulie.takin.web.biz.service.interfaceperformance;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceResultCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceResultResponse;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 10:20 上午
 */
public interface PerformanceResultService {
    /**
     * 新增结果
     *
     * @param resultCreateInput
     */
    void add(PerformanceResultCreateInput resultCreateInput);

    /**
     * 分页获取结果
     *
     * @param param
     * @return
     */
    PagingList<PerformanceResultResponse> pageResult(PerformanceResultCreateInput param);

    /**
     * 清理结果
     *
     * @param param
     */
    void flushAll(PerformanceResultCreateInput param);
}
