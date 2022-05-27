package io.shulie.takin.web.data.dao.interfaceperformance.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigDto;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceConfigQueryParam;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.entity.UserExt;
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
 * @date 2022/5/19 11:53 上午
 */
@Service
public class PerformanceConfigDAOImpl implements PerformanceConfigDAO,
        MPUtil<InterfacePerformanceConfigEntity> {
    @Resource
    private InterfacePerformanceConfigMapper interfacePerformanceConfigMapper;

    /**
     * 新增接口压测配置
     *
     * @param entity
     */
    @Override
    public Long add(InterfacePerformanceConfigEntity entity) {
        interfacePerformanceConfigMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 新增接口压测配置
     *
     * @param entity
     */
    @Override
    public void updateById(InterfacePerformanceConfigEntity entity) {
        if(entity.getId() == null){
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR,"参数未设置");
        }
        interfacePerformanceConfigMapper.updateById(entity);
    }

    @Override
    public InterfacePerformanceConfigEntity queryConfigByName(String name) {
        QueryWrapper<InterfacePerformanceConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        wrapper.eq("is_deleted", 0);
        wrapper.last("limit 1");
        return interfacePerformanceConfigMapper.selectOne(wrapper);
    }

    /**
     * 分页查询数据
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PerformanceConfigDto> pageList(PerformanceConfigQueryParam param) {
        QueryWrapper<InterfacePerformanceConfigEntity> queryWrapper = this.getWrapper(param);
        Page<InterfacePerformanceConfigEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<InterfacePerformanceConfigEntity> pageList = interfacePerformanceConfigMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        List<PerformanceConfigDto> results = pageList.getRecords().stream().map(entity -> {
            PerformanceConfigDto result = new PerformanceConfigDto();
            BeanUtils.copyProperties(entity, result);
            return result;
        }).collect(Collectors.toList());
        return PagingList.of(results, pageList.getTotal());
    }

    @Override
    public void delete(Long id) {
        interfacePerformanceConfigMapper.deleteById(id);
    }

    public QueryWrapper<InterfacePerformanceConfigEntity> getWrapper(PerformanceConfigQueryParam param) {
        QueryWrapper<InterfacePerformanceConfigEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊匹配
        if (StringUtils.isNotBlank(param.getQueryName())) {
            queryWrapper.like("name", param.getQueryName());
        }
        return queryWrapper;
    }
}
