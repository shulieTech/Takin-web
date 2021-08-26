package com.pamirs.takin.entity.dao.isolation;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.TIsolationAppRegConfigDTO;
import com.pamirs.takin.entity.domain.entity.TIsolationAppRegConfig;
import com.pamirs.takin.entity.domain.vo.TIsolationAppRegConfigVO;

public interface TIsolationAppRegConfigMapper {

    List<TIsolationAppRegConfigDTO> selectListByVO(TIsolationAppRegConfigVO tIsolationAppRegConfigVO);

    int deleteByRegIds(List<Long> regIds);

    int insert(TIsolationAppRegConfig tIsolationAppRegConfig);

    TIsolationAppRegConfig selectByRegId(Long regId);

    int updateByRegId(TIsolationAppRegConfig tIsolationAppRegConfig);

}
