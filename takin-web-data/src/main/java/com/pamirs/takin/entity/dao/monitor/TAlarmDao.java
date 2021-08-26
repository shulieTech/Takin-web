package com.pamirs.takin.entity.dao.monitor;

import java.util.List;

import com.pamirs.takin.entity.dao.common.BaseDao;
import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.query.TAlarmQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报警查询dao
 */
@Mapper
public interface TAlarmDao extends BaseDao<TAlarm> {

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    List<TAlarm> selectList(TAlarmQuery query);

    /**
     * 查询列表数
     *
     * @param query
     * @return
     */
    long selectListCount(TAlarmQuery query);

}
