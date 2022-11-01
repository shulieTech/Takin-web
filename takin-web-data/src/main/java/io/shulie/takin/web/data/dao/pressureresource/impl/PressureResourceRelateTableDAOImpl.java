package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapperV2;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PressureResourceRelateTableMapperV2 pressureResourceRelateTableMapperV2;

    @Autowired
    private ApplicationDsDbTableDAO dsDbTableDAO;

    /**
     * 新增
     *
     * @param dsEntitys
     */
    @Override
    public void add_V2(List<PressureResourceRelateTableEntityV2> dsEntitys) {
        if (CollectionUtils.isEmpty(dsEntitys)) {
            return;
        }
        dsEntitys.stream().forEach(dsEntity -> {
            pressureResourceRelateTableMapperV2.saveOrUpdate(dsEntity);
        });
    }


    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @Override
    public PagingList<RelateTableEntity> pageList_v2(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntityV2> queryWrapper = this.getWrapper_v2(param);
        Page<PressureResourceRelateTableEntityV2> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelateTableEntityV2> pageList = pressureResourceRelateTableMapperV2.selectPage(page, queryWrapper);
        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(fixTable(pageList.getRecords()), pageList.getTotal());
    }

    /**
     * 查询
     *
     * @param param
     * @return
     */
    @Override
    public List<RelateTableEntity> queryList_v2(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntityV2> queryWrapper = this.getWrapper_v2(param);
        List<PressureResourceRelateTableEntityV2> list = pressureResourceRelateTableMapperV2.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        List<RelateTableEntity> rsList = fixTable(list);
        return rsList;
    }

    private List<RelateTableEntity> fixTable(List<PressureResourceRelateTableEntityV2> list) {
        List<RelateTableEntity> rsList = Lists.newArrayList();
        // 转换关联表信息
        for (int i = 0; i < list.size(); i++) {
            PressureResourceRelateTableEntityV2 v2 = list.get(i);
            Long relateId = v2.getRelateId();
            ApplicationDsDbTableEntity dsDbTableEntity = dsDbTableDAO.getOne(relateId);
            if (dsDbTableEntity == null) {
                logger.warn("关联数据表为空{}", relateId);
                continue;
            }
            RelateTableEntity tableEntity = new RelateTableEntity();
            tableEntity.setId(v2.getId());
            tableEntity.setResourceId(v2.getResourceId());
            tableEntity.setDsKey(v2.getDsKey());
            tableEntity.setStatus(v2.getStatus());
            tableEntity.setRemark(v2.getRemark());
            tableEntity.setBusinessTable(dsDbTableEntity.getBizTable());
            tableEntity.setShadowTable(dsDbTableEntity.getShadowTable());
            tableEntity.setType(dsDbTableEntity.getManualTag() == 1 ? 0 : 1);
            // 是否选中 0=未选中，1=选中
            tableEntity.setJoinFlag(dsDbTableEntity.getIsCheck() == 1 ? 0 : 1);
            rsList.add(tableEntity);
        }
        return rsList;
    }

    @Override
    public void saveOrUpdate(List<PressureResourceRelateTableEntityV2> tableEntitys) {
        if (CollectionUtils.isEmpty(tableEntitys)) {
            return;
        }
        tableEntitys.stream().forEach(table -> {
            //pressureResourceRelateTableMapper.saveOrUpdate(table);
        });
    }

    private QueryWrapper<PressureResourceRelateTableEntityV2> getWrapper_v2(PressureResourceTableQueryParam param) {
        QueryWrapper<PressureResourceRelateTableEntityV2> queryWrapper = new QueryWrapper<>();
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
