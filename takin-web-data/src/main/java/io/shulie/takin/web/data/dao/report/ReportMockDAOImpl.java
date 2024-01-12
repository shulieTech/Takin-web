package io.shulie.takin.web.data.dao.report;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.ReportMockMapper;
import io.shulie.takin.web.data.model.mysql.ReportMockEntity;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.param.report.ReportMockCreateParam;
import io.shulie.takin.web.data.result.report.ReportMockResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportMockDAOImpl extends ServiceImpl<ReportMockMapper, ReportMockEntity>
    implements ReportMockDAO, MPUtil<ReportMockEntity> {

    @Override
    public void insertOrUpdate(ReportMockCreateParam param) {
        ReportMockEntity entity = new ReportMockEntity();
        BeanUtils.copyProperties(param, entity);
        entity.setStartTime(DateUtil.parseDateTime(param.getStartTime()));
        entity.setEndTime(DateUtil.parseDateTime(param.getEndTime()));
        /**
         * 先使用唯一索引更新，更新失败则插入
         */
        int effectRow = this.baseMapper.updateReportMockEntity(entity);
        if(effectRow == 0) {
            this.save(entity);
        }
    }

    @Override
    public Long selectCountMockByReportId(Long reportId) {
        LambdaQueryWrapper<ReportMockEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportMockEntity::getReportId,reportId);
        return this.baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<ReportMockResult> selectByExample(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportMockEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportMockEntity::getReportId,queryParam.getReportId());
        queryWrapper.orderByAsc(ReportMockEntity::getAppName);
        queryWrapper.orderByAsc(ReportMockEntity::getMockName);
        List<ReportMockEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(list, ReportMockResult.class);
    }
}
