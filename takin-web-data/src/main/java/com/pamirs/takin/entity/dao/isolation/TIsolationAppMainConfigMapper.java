package com.pamirs.takin.entity.dao.isolation;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TIsolationAppMainConfig;
import com.pamirs.takin.entity.domain.vo.TIsolationAppConfigVO;

public interface TIsolationAppMainConfigMapper {

    List<TIsolationAppMainConfig> selectListByVO(TIsolationAppConfigVO tIsolationAppConfigVO);

    int insert(TIsolationAppMainConfig tIsolationAppMainConfig);

    TIsolationAppMainConfig selectByApplicationId(Long applicationId);

    List<TIsolationAppMainConfig> selectByApplicationIds(List<Long> applicationIds);

    int updateByApplicationId(TIsolationAppMainConfig tIsolationAppMainConfig);

    int deleteByApplicationIds(List<Long> applicationIds);

}
