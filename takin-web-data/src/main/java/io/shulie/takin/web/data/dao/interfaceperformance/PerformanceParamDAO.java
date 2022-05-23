package io.shulie.takin.web.data.dao.interfaceperformance;

import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceParamVO;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceParamQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:53 上午
 */
public interface PerformanceParamDAO {
    void deleteByIds(List<Long> ids);

    void add(List<InterfacePerformanceParamEntity> insertEntitys);

    List<PerformanceParamVO> queryParamByCondition(PerformanceParamQueryParam param);
}
