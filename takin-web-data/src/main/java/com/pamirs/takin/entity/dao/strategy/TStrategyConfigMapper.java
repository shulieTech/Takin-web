package com.pamirs.takin.entity.dao.strategy;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.strategy.StrategyConfig;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigQueryVO;

public interface TStrategyConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StrategyConfig record);

    StrategyConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StrategyConfig record);

    List<StrategyConfig> getPageList(StrategyConfigQueryVO queryVO);
}
