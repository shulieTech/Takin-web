package com.pamirs.takin.entity.dao.isolation;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TIsolationAppItemConfig;
import com.pamirs.takin.entity.domain.vo.TIsolationAppItemConfigVO;

public interface TIsolationAppItemConfigMapper {

    int insert(TIsolationAppItemConfig tIsolationAppItemConfig);

    int insertList(List<TIsolationAppItemConfig> tIsolationAppItemConfigs);

    List<TIsolationAppItemConfig> selectListByVO(TIsolationAppItemConfigVO tIsolationAppItemConfigVO);

    TIsolationAppItemConfig selectByItemId(Long itemId);

    int updateByItemId(TIsolationAppItemConfig record);

    List<TIsolationAppItemConfig> selectByAppId(Long applicationId);

    int deleteByApplicationIds(List<Long> applicationIds);

}
