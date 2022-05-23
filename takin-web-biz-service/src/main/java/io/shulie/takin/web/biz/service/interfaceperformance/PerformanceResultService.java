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
    void add(PerformanceResultCreateInput resultCreateInput);

    PagingList<PerformanceResultResponse> pageResult(PerformanceResultCreateInput param);
}
