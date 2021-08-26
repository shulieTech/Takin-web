package com.pamirs.takin.entity.dao.simplify;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.TAppMiddlewareInfo;
import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;

public interface TAppMiddlewareInfoMapper {

    int delete(Long id);

    int deleteBatch(List<Long> ids);

    int insert(TAppMiddlewareInfo record);

    TAppMiddlewareInfo selectOneById(Long id);

    List<TAppMiddlewareInfo> selectList(AppMiddlewareQuery info);

    int update(TAppMiddlewareInfo record);

    int updateByPrimaryKey(TAppMiddlewareInfo record);
}
