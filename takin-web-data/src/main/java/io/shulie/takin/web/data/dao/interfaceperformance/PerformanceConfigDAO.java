package io.shulie.takin.web.data.dao.interfaceperformance;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigVO;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceConfigQueryParam;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:53 上午
 */
public interface PerformanceConfigDAO {
    void add(InterfacePerformanceConfigEntity entity);

    /**
     * 获取name是否唯一
     *
     * @param name
     * @return
     */
    InterfacePerformanceConfigEntity queryConfigByName(String name);

    /**
     * 分页查询数据
     *
     * @param param
     * @return
     */
    PagingList<PerformanceConfigVO> pageList(PerformanceConfigQueryParam param);

    /**
     * 逻辑删除
     *
     * @param id
     */
    void delete(Long id);
}
