package com.pamirs.takin.cloud.entity.dao.schedule;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.cloud.entity.domain.vo.schedule.ScheduleRecordQueryVO;

/**
 * @author -
 */
public interface TScheduleRecordMapper {
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
     * @param record -
     * @return -
     */
    int insertSelective(ScheduleRecord record);

    /**
     * 依据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    ScheduleRecord selectByPrimaryKey(Long id);

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包含主键)
     * @return -
     */
    int updateByPrimaryKey(ScheduleRecord record);

    /**
     * 分页查询
     *
     * @param queryVO -
     * @return -
     */
    List<ScheduleRecord> getPageList(ScheduleRecordQueryVO queryVO);

    /**
     * 根据任务主键获取
     *
     * @param taskId 任务主键
     * @return -
     */
    ScheduleRecord getScheduleByTaskId(Long taskId);

    /**
     * 根据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(ScheduleRecord record);

}
