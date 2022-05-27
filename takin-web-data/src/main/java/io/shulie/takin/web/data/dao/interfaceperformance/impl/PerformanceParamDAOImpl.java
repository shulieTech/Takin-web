package io.shulie.takin.web.data.dao.interfaceperformance.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceParamDto;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceParamDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceParamMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceParamEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceParamQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:53 上午
 */
@Service
public class PerformanceParamDAOImpl implements PerformanceParamDAO {
    @Autowired
    private InterfacePerformanceParamMapper interfacePerformanceParamMapper;

    /**
     * 清理掉接口压测参数
     *
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        interfacePerformanceParamMapper.deleteBatchIds(ids);
    }

    @Override
    public void add(List<InterfacePerformanceParamEntity> insertEntitys) {
        if (CollectionUtils.isEmpty(insertEntitys)) {
            return;
        }
        insertEntitys.stream().forEach(insertEntity -> {
            interfacePerformanceParamMapper.insert(insertEntity);
        });
    }

    @Override
    public List<PerformanceParamDto> queryParamByCondition(PerformanceParamQueryParam param) {
        QueryWrapper<InterfacePerformanceParamEntity> queryWrapper = getWrapper(param);
        // 更新倒序
        queryWrapper.orderByDesc("gmt_modified");
        List<InterfacePerformanceParamEntity> resultList = interfacePerformanceParamMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultList)) {
            return Collections.EMPTY_LIST;
        }
        return resultList.stream().map(resultEntity -> {
            PerformanceParamDto vo = new PerformanceParamDto();
            BeanUtils.copyProperties(resultEntity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    public QueryWrapper<InterfacePerformanceParamEntity> getWrapper(PerformanceParamQueryParam param) {
        QueryWrapper<InterfacePerformanceParamEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        if (param.getConfigId() != null) {
            queryWrapper.eq("config_id", param.getConfigId());
        }
        if (CollectionUtils.isNotEmpty(param.getFileIds())) {
            queryWrapper.in("file_id", param.getFileIds());
        }
        return queryWrapper;
    }
}
