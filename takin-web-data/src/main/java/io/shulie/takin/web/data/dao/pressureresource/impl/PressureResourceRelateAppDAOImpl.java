package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateAppDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateAppMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/31 2:37 PM
 */
@Service
public class PressureResourceRelateAppDAOImpl implements PressureResourceRelateAppDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelateAppDAOImpl.class);

    @Resource
    private PressureResourceRelateAppMapper pressureResourceRelateAppMapper;

    /**
     * 分页
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateAppEntity> pageList(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelateAppEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateAppEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelateAppEntity> pageList = pressureResourceRelateAppMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    @Override
    public List<PressureResourceRelateAppEntity> queryList(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelateAppEntity> queryWrapper = this.getWrapper(param);
        return pressureResourceRelateAppMapper.selectList(queryWrapper);
    }

    /**
     * 保存或更新
     *
     * @param list
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateAppEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        pressureResourceRelateAppMapper.saveOrUpdate(list);
    }

    private QueryWrapper<PressureResourceRelateAppEntity> getWrapper(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelateAppEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        if (StringUtils.isNotBlank(param.getAppName())) {
            queryWrapper.like("app_name", param.getAppName());
        }
        if (CollectionUtils.isNotEmpty(param.getAppNames())) {
            queryWrapper.in("app_name", param.getAppNames());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (param.getJoinPressure() != null) {
            queryWrapper.eq("join_pressure", param.getJoinPressure());
        }
        if (param.getDetailId() != null) {
            queryWrapper.eq("detail_id", param.getDetailId());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (CollectionUtils.isNotEmpty(param.getResourceIds())) {
            queryWrapper.in("resource_id", param.getResourceIds());
        }
        return queryWrapper;
    }
}
