package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class PressureResourceRelateTableDAOImpl
        implements PressureResourceRelateTableDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelateTableDAOImpl.class);

    @Resource
    private PressureResourceRelateTableMapper pressureResourceRelateTableMapper;

    /**
     * 新增
     *
     * @param dsEntitys
     */
    @Override
    public void add(List<PressureResourceRelateTableEntity> dsEntitys) {
        if (CollectionUtils.isEmpty(dsEntitys)) {
            return;
        }
        dsEntitys.stream().forEach(dsEntity -> {
            pressureResourceRelateTableMapper.insert(dsEntity);
        });
    }

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateTableEntity> pageList(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateTableEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelateTableEntity> pageList = pressureResourceRelateTableMapper.selectPage(page, queryWrapper);
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
    public List<PressureResourceRelateTableEntity> queryList(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelateTableEntity> list = pressureResourceRelateTableMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public void saveOrUpdate(List<PressureResourceRelateTableEntity> tableEntitys) {
        if (CollectionUtils.isEmpty(tableEntitys)) {
            return;
        }
        tableEntitys.stream().forEach(table -> {
            pressureResourceRelateTableMapper.saveOrUpdate(table);
        });
    }

    private QueryWrapper<PressureResourceRelateTableEntity> getWrapper(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntity> queryWrapper = new QueryWrapper<>();
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
        if (StringUtils.isNotBlank(param.getDsKey())) {
            queryWrapper.eq("ds_key", param.getDsKey());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        return queryWrapper;
    }
}
