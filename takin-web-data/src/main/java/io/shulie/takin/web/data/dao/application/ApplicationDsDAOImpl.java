package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.common.secure.SecureUtil;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsManageMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsDeleteParam;
import io.shulie.takin.web.data.param.application.ApplicationDsEnableParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateUserParam;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fanxx
 * @date 2020/11/9 8:28 下午
 */
@Component
public class ApplicationDsDAOImpl implements ApplicationDsDAO {

    @Autowired
    private ApplicationDsManageMapper applicationDsManageMapper;

    Logger logger = LoggerFactory.getLogger(ApplicationDsDAOImpl.class);

    @Override
    public int insert(ApplicationDsCreateParam createParam) {
        ApplicationDsManageEntity entity = new ApplicationDsManageEntity();
        entity.setApplicationId(createParam.getApplicationId());
        entity.setApplicationName(createParam.getApplicationName());
        entity.setDbType(createParam.getDbType());
        entity.setDsType(createParam.getDsType());
        entity.setUrl(createParam.getUrl());
        entity.setConfig(createParam.getConfig());
        entity.setParseConfig(createParam.getParseConfig());
        entity.setTenantId(createParam.getTenantId());
        entity.setUserId(createParam.getUserId());
        entity.setStatus(createParam.getStatus());
        entity.setConfigType(createParam.getConfigType());
        entity.setIsManual(createParam.isManual());
        //数据加密处理
        aes(entity);
        applicationDsManageMapper.insert(entity);
        return entity.getId().intValue();
    }

    /**
     * 数据加密通用处理
     *
     * @param entity 实体
     */
    private void aes(ApplicationDsManageEntity entity) {
        if (StringUtil.isNotBlank(entity.getConfig())) {
            entity.setConfig(SecureUtil.encryption(entity.getConfig()));
        }
        if (StringUtil.isNotBlank(entity.getParseConfig())) {
            entity.setParseConfig(SecureUtil.encryption(entity.getParseConfig()));
        }
    }

    /**
     * 数据解密密通用处理
     *
     * @param entity 实体
     */
    private void des(ApplicationDsManageEntity entity) {
        String config = entity.getConfig();
        String parseConfig = entity.getParseConfig();
        try {
            if (StringUtil.isNotBlank(config)) {
                entity.setConfig(SecureUtil.decrypt(config));
            }
            if (StringUtil.isNotBlank(parseConfig)) {
                entity.setParseConfig(SecureUtil.decrypt(parseConfig));
            }
        } catch (Exception ex) {
            //logger.warn("解密数据源配置失败，返回原始配置； 数据源ID：{}",entity.getId());
            entity.setConfig(config);
            entity.setParseConfig(parseConfig);
        }
    }

    @Override
    public int update(ApplicationDsUpdateParam updateParam) {
        ApplicationDsManageEntity entity = new ApplicationDsManageEntity();
        entity.setId(updateParam.getId());
        entity.setUrl(updateParam.getUrl());
        entity.setStatus(updateParam.getStatus());
        entity.setConfig(updateParam.getConfig());
        entity.setParseConfig(updateParam.getParseConfig());

        aes(entity);
        return applicationDsManageMapper.updateById(entity);
    }

    @Override
    public int enable(ApplicationDsEnableParam enableParam) {
        ApplicationDsManageEntity entity = new ApplicationDsManageEntity();
        entity.setId(enableParam.getId());
        entity.setStatus(enableParam.getStatus());
        return applicationDsManageMapper.updateById(entity);
    }

    @Override
    public int delete(ApplicationDsDeleteParam deleteParam) {
        if (CollectionUtils.isNotEmpty(deleteParam.getIdList())) {
            return applicationDsManageMapper.deleteBatchIds(deleteParam.getIdList());
        }
        return 0;
    }

    @Override
    public ApplicationDsResult queryByPrimaryKey(Long id) {
        ApplicationDsManageEntity entity = applicationDsManageMapper.selectById(id);
        if (Objects.isNull(entity)) {
            return null;
        }

        des(entity);

        ApplicationDsResult dsResult = new ApplicationDsResult();
        dsResult.setId(entity.getId());
        dsResult.setApplicationId(entity.getApplicationId());
        dsResult.setApplicationName(entity.getApplicationName());
        dsResult.setDbType(entity.getDbType());
        dsResult.setDsType(entity.getDsType());
        dsResult.setUrl(entity.getUrl());
        dsResult.setStatus(entity.getStatus());
        dsResult.setUpdateTime(entity.getUpdateTime());
        dsResult.setConfig(entity.getConfig());
        dsResult.setParseConfig(entity.getParseConfig());
        dsResult.setTenantId(entity.getTenantId());
        dsResult.setUserId(entity.getUserId());
        dsResult.setConfigType(entity.getConfigType());
        return dsResult;
    }

    @Override
    public List<ApplicationDsResult> queryList(ApplicationDsQueryParam param) {
        LambdaQueryWrapper<ApplicationDsManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!Objects.isNull(param.getApplicationId())) {
            queryWrapper.eq(ApplicationDsManageEntity::getApplicationId, param.getApplicationId());
        }
        if (!Objects.isNull(param.getStatus())) {
            queryWrapper.eq(ApplicationDsManageEntity::getStatus, param.getStatus());
        }
        if (!Objects.isNull(param.getIsDeleted())) {
            queryWrapper.eq(ApplicationDsManageEntity::getIsDeleted, param.getIsDeleted());
        }
        if (CollectionUtils.isNotEmpty(param.getUserIdList())) {
            queryWrapper.in(ApplicationDsManageEntity::getUserId, param.getUserIdList());
        }
        if (StringUtils.isNotBlank(param.getUrl())) {
            queryWrapper.eq(ApplicationDsManageEntity::getUrl, param.getUrl());
        }
        queryWrapper.orderByDesc(ApplicationDsManageEntity::getUpdateTime);
        List<ApplicationDsManageEntity> dsManageEntityList = applicationDsManageMapper.selectList(queryWrapper);
        List<ApplicationDsResult> applicationDsResultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dsManageEntityList)) {
            for (ApplicationDsManageEntity entity : dsManageEntityList) {
                des(entity);
                ApplicationDsResult dsResult = new ApplicationDsResult();
                dsResult.setId(entity.getId());
                dsResult.setApplicationId(entity.getApplicationId());
                dsResult.setApplicationName(entity.getApplicationName());
                dsResult.setDbType(entity.getDbType());
                dsResult.setDsType(entity.getDsType());
                dsResult.setUrl(entity.getUrl());
                dsResult.setStatus(entity.getStatus());
                dsResult.setUpdateTime(entity.getUpdateTime());
                dsResult.setConfig(entity.getConfig());
                dsResult.setParseConfig(entity.getParseConfig());
                dsResult.setTenantId(entity.getTenantId());
                dsResult.setUserId(entity.getUserId());
                dsResult.setManual(entity.getIsManual());
                applicationDsResultList.add(dsResult);
            }
        }
        return applicationDsResultList;
    }

    @Override
    public int allocationUser(ApplicationDsUpdateUserParam param) {
        if (Objects.isNull(param.getApplicationId())) {
            return 0;
        }
        LambdaQueryWrapper<ApplicationDsManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationDsManageEntity::getApplicationId, param.getApplicationId());
        List<ApplicationDsManageEntity> applicationDsManageEntityList = applicationDsManageMapper.selectList(
                queryWrapper);
        if (CollectionUtils.isNotEmpty(applicationDsManageEntityList)) {
            for (ApplicationDsManageEntity entity : applicationDsManageEntityList) {
                entity.setUserId(param.getUserId());
                applicationDsManageMapper.updateById(entity);
            }
        }
        return 0;
    }

    @Override
    public List<DsModelWithBLOBs> selectByAppIdForAgent(Long applicationId) {
        LambdaQueryWrapper<ApplicationDsManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationDsManageEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationDsManageEntity::getStatus, 0);
        List<ApplicationDsManageEntity> dsManageEntityList = applicationDsManageMapper.selectList(queryWrapper);

        List<DsModelWithBLOBs> applicationDsResultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dsManageEntityList)) {
            for (ApplicationDsManageEntity entity : dsManageEntityList) {
                des(entity);
                DsModelWithBLOBs dsResult = new DsModelWithBLOBs();
                BeanUtils.copyProperties(entity, dsResult);
                dsResult.setDsType(entity.getDsType().byteValue());
                dsResult.setDbType(entity.getDbType().byteValue());
                dsResult.setStatus(entity.getStatus().byteValue());
                applicationDsResultList.add(dsResult);
            }
        }
        return applicationDsResultList;
    }

    @Override
    public List<DsModelWithBLOBs> getAllEnabledDbConfig(Long applicationId) {
        return selectByAppIdForAgent(applicationId);
    }

    @Override
    @Transactional
    public void batchDelete(ApplicationDsDeleteParam deleteParam) {
        List<Long> idList = deleteParam.getIdList();
        idList.forEach(id -> {
            ApplicationDsManageEntity entity = new ApplicationDsManageEntity();
            entity.setId(id);
            entity.setStatus(1);
            entity.setIsDeleted(1);
            applicationDsManageMapper.updateById(entity);
        });
    }

    @Override
    public void dsDeleteByAppName(String appName) {
        LambdaQueryWrapper<ApplicationDsManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationDsManageEntity::getApplicationName, appName);
        queryWrapper.eq(ApplicationDsManageEntity::getStatus, 0);
        List<ApplicationDsManageEntity> dsManageEntityList = applicationDsManageMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(dsManageEntityList)) {
            List<Long> ids = dsManageEntityList.stream().map(ApplicationDsManageEntity::getId).collect(Collectors.toList());
            ApplicationDsDeleteParam applicationDsDeleteParam = new ApplicationDsDeleteParam();
            applicationDsDeleteParam.setIdList(ids);
            this.batchDelete(applicationDsDeleteParam);
        }
    }

}
