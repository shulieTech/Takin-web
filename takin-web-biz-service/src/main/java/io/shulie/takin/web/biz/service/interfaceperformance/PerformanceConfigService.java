package io.shulie.takin.web.biz.service.interfaceperformance;

import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDebugRequest;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigVO;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:45 下午
 */
public interface PerformanceConfigService {
    Long add(PerformanceConfigCreateInput input);

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

    PerformanceConfigVO detail(Long configId);
	
	ResponseResult<SceneActionResp> start(SceneActionParam param);
}
