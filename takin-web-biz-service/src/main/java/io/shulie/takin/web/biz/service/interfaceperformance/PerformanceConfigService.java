package io.shulie.takin.web.biz.service.interfaceperformance;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigVO;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:45 下午
 */
public interface PerformanceConfigService {
    void add(PerformanceConfigCreateInput input);

    // update
    void update(PerformanceConfigCreateInput input);

    /**
     * 删除
     *
     * @param configId
     */
    void delete(Long configId);

    /**
     * 分页查询
     *
     * @param input
     * @return
     */
    PagingList<PerformanceConfigVO> query(PerformanceConfigQueryRequest input);
}
