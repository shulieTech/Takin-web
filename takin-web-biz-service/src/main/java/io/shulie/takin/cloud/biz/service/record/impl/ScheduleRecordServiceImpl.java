package io.shulie.takin.cloud.biz.service.record.impl;

import java.util.List;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.dao.schedule.TScheduleRecordMapper;
import com.pamirs.takin.cloud.entity.domain.dto.schedule.ScheduleRecordDTO;
import com.pamirs.takin.cloud.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.cloud.entity.domain.vo.schedule.ScheduleRecordQueryVO;
import io.shulie.takin.cloud.biz.service.record.ScheduleRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/5/9 下午2:16
 */
@Slf4j
@Service
public class ScheduleRecordServiceImpl implements ScheduleRecordService {

    @Resource
    private TScheduleRecordMapper tScheduleRecordMapper;

    @Override
    public PageInfo<ScheduleRecordDTO> queryPageList(ScheduleRecordQueryVO queryVO) {
        Page<ScheduleRecord> page = PageHelper.startPage(queryVO.getCurrentPage() + 1, queryVO.getPageSize());

        List<ScheduleRecord> queryList = tScheduleRecordMapper.getPageList(queryVO);
        if (CollectionUtils.isEmpty(queryList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<ScheduleRecordDTO> resultList = Lists.newArrayList();
        queryList.forEach(data -> {
            ScheduleRecordDTO dto = new ScheduleRecordDTO();
            dto.setId(data.getId());
            dto.setPodNum(data.getPodNum());
            dto.setPodClass(data.getPodClass());
            dto.setStatusInt(data.getStatus());
            dto.setMemorySize(data.getMemorySize());
            dto.setCpuCoreNum(data.getCpuCoreNum());
            dto.setCreateTime(DateUtil.formatDateTime(data.getCreateTime()));
            resultList.add(dto);
        });

        PageInfo<ScheduleRecordDTO> pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
