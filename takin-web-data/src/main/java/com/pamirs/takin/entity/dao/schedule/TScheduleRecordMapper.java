package com.pamirs.takin.entity.dao.schedule;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.entity.domain.vo.schedule.ScheduleRecordQueryVO;

public interface TScheduleRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ScheduleRecord record);

    ScheduleRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ScheduleRecord record);

    List<ScheduleRecord> getPageList(ScheduleRecordQueryVO queryVO);
}
