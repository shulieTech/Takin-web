package io.shulie.takin.web.biz.service;

import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.query.Result;
import com.pamirs.takin.entity.domain.query.ResultList;
import com.pamirs.takin.entity.domain.query.TAlarmQuery;

/**
 * 说明: 告警相关服务实接口
 */
public interface TAlarmService {

    Result<Void> add(TAlarm tAlarm);

    Result<Void> modify(TAlarm tAlarm);

    Result<Void> deleteById(Long id);

    ResultList<TAlarm> queryListByQuery(TAlarmQuery query);

    Result<TAlarm> queryOneById(Long id);

}
