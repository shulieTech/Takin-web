package com.pamirs.takin.cloud.entity.dao.strategy;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.strategy.StrategyConfig;
import com.pamirs.takin.cloud.entity.domain.vo.strategy.StrategyConfigQueryVO;

/**
 * 策略配置 mapper
 *
 * @author -
 */
public interface TStrategyConfigMapper {
    /**
     * 依据主键删除
     *
     * @param id 数据主键
     * @return -
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入
     *
     * @param record 数据内容
     * @return -
     */
    int insert(StrategyConfig record);

    /**
     * 依据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    StrategyConfig selectByPrimaryKey(Long id);

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(StrategyConfig record);

    /**
     * 分页查询
     *
     * @param queryVO -
     * @return -
     */
    List<StrategyConfig> getPageList(StrategyConfigQueryVO queryVO);
}
