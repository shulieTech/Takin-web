package io.shulie.takin.web.biz.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.cache.agentimpl.ApplicationPluginConfigAgentCache;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.service.ApplicationPluginsConfigService;
import io.shulie.takin.web.biz.utils.CopyUtils;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationPluginsConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationPluginsConfigMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginsConfigEntity;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginsConfigVO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * (ApplicationPluginsConfig)表服务实现类
 *
 * @author caijy
 * @since 2021-05-18 17:22:42
 */
@Slf4j
@Service
public class ApplicationPluginsConfigServiceImpl implements ApplicationPluginsConfigService {

    @Autowired
    ApplicationPluginsConfigDAO applicationPluginsConfigDAO;

    @Resource
    ApplicationPluginsConfigMapper applicationPluginsConfigMapper;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationPluginConfigAgentCache applicationPluginConfigAgentCache;

    @Override
    public ApplicationPluginsConfigVO getById(Long id) {
        if (id != null) {
            ApplicationPluginsConfigEntity configEntity = applicationPluginsConfigDAO.getById(id);
            return CopyUtils.copyFields(configEntity, ApplicationPluginsConfigVO.class);
        }
        return null;
    }

    @Override
    public PagingList<ApplicationPluginsConfigVO> getPageByParam(ApplicationPluginsConfigParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getApplicationId())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "缺少参数");
        }
        Long tenantId = WebPluginUtils.traceTenantId();
        param.setTenantId(tenantId);
        IPage<ApplicationPluginsConfigEntity> listPage = applicationPluginsConfigDAO.findListPage(param);
        List<ApplicationPluginsConfigEntity> records = listPage.getRecords();
        List<ApplicationPluginsConfigVO> configVos = Lists.newArrayList();
        for (ApplicationPluginsConfigEntity record : records) {
            ApplicationPluginsConfigVO configVO = new ApplicationPluginsConfigVO();
            BeanUtils.copyProperties(record, configVO);
            if ("-1".equals(configVO.getConfigValue())) {
                configVO.setConfigValueName("与业务key一致");
            } else {
                configVO.setConfigValueName(record.getConfigValue() + "小时");
            }
            //精度丢失问题
            configVO.setApplicationId(record.getApplicationId() + "");
            configVO.setId(record.getId() + "");
            configVos.add(configVO);
        }
        return PagingList.of(configVos, listPage.getTotal());
    }

    @Override
    public Boolean add(ApplicationPluginsConfigParam param) {
        ApplicationPluginsConfigEntity entity = this.validateAndSet(param);
        applicationPluginsConfigMapper.insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addBatch(List<ApplicationPluginsConfigParam> params) {
        List<ApplicationPluginsConfigEntity> entityList = Lists.newArrayList();
        params.forEach(t -> {
            entityList.add(this.validateAndSet(t));
        });
        applicationPluginsConfigDAO.saveBatch(entityList);
        return true;
    }

    private ApplicationPluginsConfigEntity validateAndSet(ApplicationPluginsConfigParam param) {
        if (Objects.isNull(param)) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "缺少参数");
        }
        ApplicationPluginsConfigEntity entity = CopyUtils.copyFields(param, ApplicationPluginsConfigEntity.class);
        if (Objects.isNull(entity.getApplicationId())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "应用ID不能为空！");
        }
        if (StringUtil.isBlank(entity.getConfigItem())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "配置项不能为空！");
        }
        if (StringUtil.isBlank(entity.getConfigValue())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "配置值不能为空！");
        }
        //优先取参数内的 否则从UserExt取
        if (param.getUserId() != null && param.getTenantId() != null) {
            entity.setCreatorId(param.getUserId());
            entity.setModifierId(param.getUserId());
            entity.setTenantId(param.getTenantId());
        } else {
            entity.setCreatorId(WebPluginUtils.traceUserId());
            entity.setModifierId(WebPluginUtils.traceUserId());
            entity.setTenantId(WebPluginUtils.traceTenantId());
        }

        Date now = new Date();
        entity.setCreateTime(now);
        entity.setModifieTime(now);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBatch(List<ApplicationPluginsConfigParam> params) {
        params.forEach(param -> {
            if (Objects.isNull(param)) {
                throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "缺少参数");
            }
            if (Objects.isNull(param.getId())) {
                throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "ID不能为空！");
            }
        });

        List<ApplicationPluginsConfigEntity> entityList = CopyUtils.copyFieldsList(params,
            ApplicationPluginsConfigEntity.class);
        Date now = new Date();
        entityList.forEach(entity -> {
            entity.setCreateTime(now);
            entity.setModifieTime(now);
            entity.setCreatorId(WebPluginUtils.traceUserId());
            entity.setModifierId(WebPluginUtils.traceUserId());
            entity.setTenantId(WebPluginUtils.traceTenantId());
        });
        boolean flag = applicationPluginsConfigDAO.updateBatchById(entityList);
        entityList.forEach(e -> this.evict(CommonUtil.generateRedisKey(e.getApplicationName(),e.getConfigKey())));
        return flag;
    }

    /**
     * 清除缓存
     * @param namespace
     */
    private void evict(String namespace) {
        applicationPluginConfigAgentCache.evict(namespace);
    }

    @Override
    public Boolean update(ApplicationPluginsConfigParam param) {
        if (Objects.isNull(param)) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "缺少参数");
        }
        if (Objects.isNull(param.getId())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "ID不能为空！");
        }
        if (StringUtil.isEmpty(param.getConfigValue())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "配置值不能为空！");
        }
        // 配置是否存在
        ApplicationPluginsConfigEntity oldEntity = applicationPluginsConfigDAO.getById(param.getId());
        if(oldEntity == null) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "该配置不存在！");
        }
        ApplicationPluginsConfigEntity entity = CopyUtils.copyFields(param, ApplicationPluginsConfigEntity.class);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setModifieTime(now);
        entity.setCreatorId(WebPluginUtils.traceUserId());
        entity.setModifierId(WebPluginUtils.traceUserId());
        entity.setTenantId(WebPluginUtils.traceTenantId());
        applicationPluginsConfigMapper.updateById(entity);
        this.evict(CommonUtil.generateRedisKey(oldEntity.getApplicationName(),oldEntity.getConfigKey()));
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars(Vars.APPLICATION_ID,oldEntity.getApplicationId().toString());
        OperationLogContextHolder.addVars(Vars.APP_PLUGIN_KEY,oldEntity.getConfigItem());
        OperationLogContextHolder.addVars(Vars.APP_PLUGIN_VALUE,oldEntity.getConfigValue().equals("-1")?"与业务key一致":oldEntity.getConfigValue()+" h");
        return true;
    }

    @Override
    public List<ApplicationPluginsConfigVO> getListByParam(ApplicationPluginsConfigParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getApplicationName())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "缺少参数");
        }
        if (Objects.isNull(param.getConfigKey())) {
            throw new TakinWebException(ExceptionCode.POD_NUM_EMPTY, "configKey为空");
        }
        ApplicationDetailResult application = applicationDAO.getApplicationByTenantIdAndName(param.getApplicationName());
        param.setApplicationId(application.getApplicationId());
        List<ApplicationPluginsConfigEntity> list = applicationPluginsConfigDAO.findList(param);
        if (list != null && !list.isEmpty()) {
            return CopyUtils.copyFieldsList(list, ApplicationPluginsConfigVO.class);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        log.info("开始补充每个应用的插件配置");
        List<ApplicationDetailResult> applications = applicationDAO.getAllApplications();
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam();
        List<ApplicationPluginsConfigEntity> list = applicationPluginsConfigDAO.findList(param);
        List<Long> applicationIds = Lists.newArrayList();
        if (list.size() > 0) {
            applicationIds = list.stream().map(ApplicationPluginsConfigEntity::getApplicationId).collect(
                Collectors.toList());
        }
        List<Long> finalApplicationIds = applicationIds;
        List<ApplicationDetailResult> needInitList = applications.stream().filter(
            t -> !finalApplicationIds.contains(t.getApplicationId())).collect(
            Collectors.toList());
        needInitList.forEach(applicationMnt -> {
            ApplicationPluginsConfigParam configParam = new ApplicationPluginsConfigParam();
            configParam.setConfigItem("redis影子key有效期");
            configParam.setConfigKey("redis_expire");
            configParam.setConfigDesc("可自定义设置redis影子key有效期，默认与业务key有效期一致。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。");
            configParam.setConfigValue("-1");
            configParam.setApplicationName(applicationMnt.getApplicationName());
            configParam.setApplicationId(applicationMnt.getApplicationId());
            configParam.setUserId(applicationMnt.getUserId());
            configParam.setTenantId(applicationMnt.getTenantId());
            configParam.setEnvCode(applicationMnt.getEnvCode());
            this.add(configParam);
        });
    }
}
