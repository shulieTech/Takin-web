package io.shulie.takin.cloud.biz.service.record;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.cloud.entity.domain.dto.schedule.ScheduleRecordDTO;
import com.pamirs.takin.cloud.entity.domain.vo.schedule.ScheduleRecordQueryVO;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:10
 */
public interface ScheduleRecordService {
    /**
     * 分页查询列表
     *
     * @param queryVO 查询条件
     * @return 查询结果
     */
    PageInfo<ScheduleRecordDTO> queryPageList(ScheduleRecordQueryVO queryVO);
}
