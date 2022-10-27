package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapperV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntityV2;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
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
 * @date 2022/8/31 7:41 PM
 */
@Service
public class PressureResourceRelateDsDAOImpl implements PressureResourceRelateDsDAO {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRelateDsDAOImpl.class);

    @Resource
    private PressureResourceRelateDsMapper pressureResourceRelateDsMapper;

    @Resource
    private PressureResourceRelateDsMapperV2 pressureResourceRelateDsMapperV2;

    @Autowired
    private ApplicationDsDbManageDAO dbManageDAO;

    /**
     * 批量新增
     *
     * @param dsEntitys
     */
    @Override
    public void add(List<PressureResourceRelateDsEntity> dsEntitys) {
        if (CollectionUtils.isNotEmpty(dsEntitys)) {
            dsEntitys.stream().forEach(entity -> {
                pressureResourceRelateDsMapper.insert(entity);
            });
        }
    }

    /**
     * 批量新增
     *
     * @param dsEntitys
     */
    @Override
    public void add_v2(List<PressureResourceRelateDsEntityV2> dsEntitys) {
        if (CollectionUtils.isNotEmpty(dsEntitys)) {
            dsEntitys.stream().forEach(entity -> {
                pressureResourceRelateDsMapperV2.saveOrUpdateV2(entity);
            });
        }
    }

    /**
     * 按参数查询
     *
     * @return
     */
    @Override
    public List<PressureResourceRelateDsEntity> queryByParam(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelateDsEntity> resultLists = pressureResourceRelateDsMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        return resultLists;
    }

    @Override
    public List<PressureResourceRelateDsEntity> queryByParam_v2(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntityV2> queryWrapper = this.getWrapperV2(param);
        List<PressureResourceRelateDsEntityV2> resultLists = pressureResourceRelateDsMapperV2.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        List<PressureResourceRelateDsEntity> rsList = Lists.newArrayList();
        // 转换下所有数据源关联信息
        for (int i = 0; i < resultLists.size(); i++) {
            // 关联数据源信息
            PressureResourceRelateDsEntityV2 v2 = resultLists.get(i);
            ApplicationDsDbManageDetailResult dsDbManageDetailResult = dbManageDAO.selectOneById(v2.getRelateId());
            if (dsDbManageDetailResult == null) {
                logger.warn("关联数据源为空,{}", v2.getRelateId());
                continue;
            }
            PressureResourceRelateDsEntity dsEntity = RelateConvert.dsManageConvertRelateDs(dsDbManageDetailResult);
            dsEntity.setId(v2.getId());
            dsEntity.setResourceId(v2.getResourceId());
            dsEntity.setDetailId(v2.getDetailId());
            dsEntity.setStatus(v2.getStatus());
            dsEntity.setRemark(v2.getRemark());
            dsEntity.setType(v2.getType());

            rsList.add(dsEntity);
        }
        return rsList;
    }

    @Override
    public void saveOrUpdate(List<PressureResourceRelateDsEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.stream().forEach(dsEntity -> {
            pressureResourceRelateDsMapper.saveOrUpdate(dsEntity);
        });
    }

    private QueryWrapper<PressureResourceRelateDsEntity> getWrapper(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryBussinessDatabase())) {
            queryWrapper.like("business_database", param.getQueryBussinessDatabase());
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryAppName())) {
            queryWrapper.like("app_name", param.getQueryAppName());
        }
        if (StringUtils.isNotBlank(param.getBussinessDatabase())) {
            queryWrapper.eq("business_database", param.getBussinessDatabase());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        return queryWrapper;
    }


    private QueryWrapper<PressureResourceRelateDsEntityV2> getWrapperV2(PressureResourceDsQueryParam param) {
        QueryWrapper<PressureResourceRelateDsEntityV2> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryBussinessDatabase())) {
            queryWrapper.like("business_database", param.getQueryBussinessDatabase());
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getQueryAppName())) {
            queryWrapper.like("app_name", param.getQueryAppName());
        }
        if (StringUtils.isNotBlank(param.getBussinessDatabase())) {
            queryWrapper.eq("business_database", param.getBussinessDatabase());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (param.getStatus() != null) {
            queryWrapper.eq("status", param.getStatus());
        }
        if (param.getId() != null) {
            queryWrapper.eq("id", param.getId());
        }
        return queryWrapper;
    }
}
