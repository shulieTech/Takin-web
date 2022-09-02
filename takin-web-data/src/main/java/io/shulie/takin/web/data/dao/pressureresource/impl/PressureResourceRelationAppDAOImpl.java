package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationAppMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
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
public class PressureResourceRelationAppDAOImpl implements PressureResourceRelationAppDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelationAppDAOImpl.class);

    @Resource
    private PressureResourceRelationAppMapper pressureResourceRelationAppMapper;

    /**
     * 分页
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationAppEntity> pageList(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelationAppEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelationAppEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelationAppEntity> pageList = pressureResourceRelationAppMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    @Override
    public List<PressureResourceRelationAppEntity> queryList(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelationAppEntity> queryWrapper = this.getWrapper(param);
        return pressureResourceRelationAppMapper.selectList(queryWrapper);
    }

    private QueryWrapper<PressureResourceRelationAppEntity> getWrapper(PressureResourceAppQueryParam param) {
        QueryWrapper<PressureResourceRelationAppEntity> queryWrapper = new QueryWrapper<>();
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
        return queryWrapper;
    }
}
