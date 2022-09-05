package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationTableMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/1 8:38 PM
 */
@Service
public class PressureResourceRelationTableDAOImpl implements PressureResourceRelationTableDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelationTableDAOImpl.class);

    @Resource
    private PressureResourceRelationTableMapper pressureResourceRelationTableMapper;

    /**
     * 新增
     *
     * @param dsEntitys
     */
    @Override
    public void add(List<PressureResourceRelationTableEntity> dsEntitys) {
        if (CollectionUtils.isEmpty(dsEntitys)) {
            return;
        }
        dsEntitys.stream().forEach(dsEntity -> {
            pressureResourceRelationTableMapper.insert(dsEntity);
        });
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationTableEntity> pageList(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelationTableEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelationTableEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelationTableEntity> pageList = pressureResourceRelationTableMapper.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    /**
     * 查询
     *
     * @param param
     * @return
     */
    @Override
    public List<PressureResourceRelationTableEntity> queryList(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelationTableEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelationTableEntity> list = pressureResourceRelationTableMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    private QueryWrapper<PressureResourceRelationTableEntity> getWrapper(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelationTableEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryBusinessTableName())) {
            queryWrapper.like("business_table", param.getQueryBusinessTableName());
        }
        if (StringUtils.isNotBlank(param.getBusinessTableName())) {
            queryWrapper.eq("business_table", param.getBusinessTableName());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (param.getDsId() != null) {
            queryWrapper.eq("ds_id", param.getDsId());
        }
        return queryWrapper;
    }
}
