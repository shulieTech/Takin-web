package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceResultCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceResultResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceResultMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/22 3:35 下午
 */
@Slf4j
@Service
public class PerformanceResultServiceImpl implements PerformanceResultService {
    @Resource
    private InterfacePerformanceResultMapper interfacePerformanceResultMapper;

    /**
     * 新增响应结果
     *
     * @param resultCreateInput
     */
    @Override
    public void add(PerformanceResultCreateInput resultCreateInput) {
        InterfacePerformanceResultEntity insertEntity = new InterfacePerformanceResultEntity();
        BeanUtils.copyProperties(resultCreateInput, insertEntity);
        insertEntity.setGmtCreate(new Date());
        insertEntity.setGmtModified(new Date());
        interfacePerformanceResultMapper.insert(insertEntity);
    }

    @Override
    public PagingList<PerformanceResultResponse> pageResult(PerformanceResultCreateInput param) {
        QueryWrapper<InterfacePerformanceResultEntity> queryWrapper = this.getWrapper(param);
        Page<InterfacePerformanceResultEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<InterfacePerformanceResultEntity> pageList = interfacePerformanceResultMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        List<PerformanceResultResponse> results = pageList.getRecords().stream().map(entity -> {
            PerformanceResultResponse result = new PerformanceResultResponse();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
        return PagingList.of(results, pageList.getTotal());
    }

    public QueryWrapper<InterfacePerformanceResultEntity> getWrapper(PerformanceResultCreateInput param) {
        QueryWrapper<InterfacePerformanceResultEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        if (param.getConfigId() != null) {
            queryWrapper.eq("config_id", param.getConfigId());
        }
        if (StringUtils.isNotBlank(param.getResultId())) {
            queryWrapper.eq("result_id", param.getResultId());
        }
        return queryWrapper;
    }
}
