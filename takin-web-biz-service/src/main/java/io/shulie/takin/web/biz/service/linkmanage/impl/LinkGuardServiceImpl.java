package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import com.pamirs.takin.entity.dao.linkguard.TLinkGuardMapper;
import com.pamirs.takin.entity.domain.entity.LinkGuardEntity;
import com.pamirs.takin.entity.domain.query.LinkGuardQueryParam;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.linkmanage.LinkGuardService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.GuardEnableConstants;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.LinkGuardDAO;
import io.shulie.takin.web.data.param.application.LinkGuardCreateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.linkguard.LinkGuardResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 慕白
 * @date 2020-03-05 10:42
 */

@Service
@Slf4j
public class LinkGuardServiceImpl implements LinkGuardService {

    private static String FALSE_CORE = "0";
    @Resource
    private TLinkGuardMapper tLinkGuardMapper;
    @Autowired
    private ApplicationService applicationService;
    @Resource
    @Autowired
    private ConfigSyncService configSyncService;
    @Autowired
    private LinkGuardDAO linkGuardDAO;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    private ApplicationDAO applicationDAO;


    @Override
    public Response addGuard(LinkGuardVo vo) {
        if (StringUtils.isBlank(vo.getApplicationId())) {
            LinkGuardEntity linkGuardEntity = tLinkGuardMapper.selectById(vo.getId());
        } else {
        }
        if (StringUtils.isBlank(vo.getApplicationName()) || StringUtils.isBlank(vo.getMethodInfo())) {
            return Response.fail(FALSE_CORE, "applicationName和methodInfo不能为空");
        }
        if (!vo.getMethodInfo().contains("#")) {
            return Response.fail(FALSE_CORE, "类名方法名用'#'分割，如Aa#bb");
        }
        LinkGuardQueryParam param = new LinkGuardQueryParam();
        param.setMethodInfo(vo.getMethodInfo());
        if (vo.getApplicationId() != null && !vo.getApplicationId().isEmpty()) {
            param.setAppId(Long.valueOf(vo.getApplicationId()));
        }
        List<LinkGuardEntity> dbList = tLinkGuardMapper.selectByExample(param, null);
        if (dbList != null && dbList.size() > 0) {
            return Response.fail(FALSE_CORE, "同一个methodInfo只能设置一个挡板");
        }
        LinkGuardCreateParam createParam = new LinkGuardCreateParam();
        createParam.setIsEnable(vo.getIsEnable());
        createParam.setApplicationName(vo.getApplicationName());
        createParam.setMethodInfo(vo.getMethodInfo());
        createParam.setGroovy(vo.getGroovy());
        createParam.setApplicationId(Long.valueOf(vo.getApplicationId()));
        createParam.setRemark(vo.getRemark());
        WebPluginUtils.fillUserData(createParam);
        try {
            linkGuardDAO.insert2(createParam);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(FALSE_CORE, "创建挡板失败");
        }
        applicationService.modifyAccessStatus(vo.getApplicationId(), AppAccessTypeEnum.UNUPLOAD.getValue(), null);
        configSyncService.syncGuard(WebPluginUtils.traceTenantCommonExt(), Long.parseLong(vo.getApplicationId()), vo.getApplicationName());
        //todo agent改造点
        agentConfigCacheManager.evictGuards(vo.getApplicationName());
        return Response.success();
    }

    @Override
    public Response updateGuard(LinkGuardVo vo) {
        if (vo.getId() == null) {
            return Response.fail(FALSE_CORE, "更新挡板id不能为null", null);
        }
        String applicationId;
        if (StringUtils.isBlank(vo.getApplicationId())) {
            LinkGuardEntity linkGuardEntity = tLinkGuardMapper.selectById(vo.getId());
            applicationId = String.valueOf(linkGuardEntity.getApplicationId());
        } else {
            applicationId = vo.getApplicationId();
        }
        LinkGuardEntity entity = new LinkGuardEntity();
        entity.setId(vo.getId());
        entity.setApplicationName(vo.getApplicationName());
        entity.setMethodInfo(vo.getMethodInfo());
        entity.setGroovy(vo.getGroovy());
        if (Objects.nonNull(vo.getIsEnable())) {
            entity.setIsEnable(vo.getIsEnable() ? GuardEnableConstants.GUARD_ENABLE : GuardEnableConstants.GUARD_UNABLE);
        }
        entity.setRemark(vo.getRemark());
        try {
            tLinkGuardMapper.update(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(FALSE_CORE, "更新挡板失败", null);
        }
        // 原先是 用户基本的的key ，现在改成 租户级别的
        configSyncService.syncGuard(WebPluginUtils.traceTenantCommonExt(), Long.parseLong(applicationId), vo.getApplicationName());
        //todo agent改造点
        agentConfigCacheManager.evictGuards(vo.getApplicationName());
        return Response.success();
    }

    @Override
    public Response deleteById(Long id) {
        try {
            LinkGuardEntity linkGuardEntity = tLinkGuardMapper.selectById(id);
            tLinkGuardMapper.deleteById(id);
            configSyncService.syncGuard(WebPluginUtils.traceTenantCommonExt(), linkGuardEntity.getApplicationId(), null);
            //todo agent改造点
            agentConfigCacheManager.evictGuards(linkGuardEntity.getApplicationName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(FALSE_CORE, "删除挡板失败", null);
        }
        return Response.success();
    }

    @Override
    public Response<List<LinkGuardVo>> selectByExample(LinkGuardQueryParam param) {
        List<LinkGuardVo> result = new ArrayList<>();
        List<LinkGuardEntity> page = new ArrayList<>();
        List<LinkGuardEntity> list = null;
        if (param != null && StringUtils.isNotBlank(param.getApplicationId())) {
            param.setAppId(Long.valueOf(param.getApplicationId()));
        }

        //处理agent携带用户信息的查询
        if (WebPluginUtils.traceTenantAppKey() != null && !WebPluginUtils.traceTenantAppKey().isEmpty()) {
            if (param.getApplicationName() != null) {
                ApplicationDetailResult applicationMnt = applicationService.queryTApplicationMntByName(param.getApplicationName());
                if (applicationMnt != null) {
                    param.setAppId(applicationMnt.getApplicationId());
                }
            }
        }
        list = tLinkGuardMapper.selectByExample(param, null);

        if (null != list && list.size() > 0) {
            if (param.getCurrentPage() == null || param.getPageSize() == null) {
                page = list;
            } else {
                page = PageUtils.getPage(true, param.getCurrentPage(), param.getPageSize(), list);
            }
            page.stream().forEach(guardEntity -> {
                result.add(entityToVo(guardEntity));
            });
        }
        return Response.success(result, CollectionUtils.isEmpty(list) ? 0 : list.size());
    }

    @Override
    public List<LinkGuardVo> agentSelect(String appName) {
        List<LinkGuardResult> results = linkGuardDAO.selectByAppNameUnderCurrentUser(appName);
        return results.stream().map(item -> {
            LinkGuardVo target = new LinkGuardVo();
            BeanUtils.copyProperties(item, target);
            return target;
        }).collect(Collectors.toList());
    }

    @Override
    public Response<List<LinkGuardEntity>> selectAll() {
        List<LinkGuardEntity> list = null;
        try {
            LinkGuardQueryParam param = new LinkGuardQueryParam();
            param.setIsEnable(true);
            list = tLinkGuardMapper.selectByExample(param,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.fail(FALSE_CORE, "查询挡板失败", null);
        }
        return Response.success(list);
    }

    @Override
    public Response getById(Long id) {
        LinkGuardEntity guardEntity = tLinkGuardMapper.selectById(id);
        LinkGuardVo vo = entityToVo(guardEntity);
        return Response.success(vo);
    }

    @Override
    public Response enableGuard(Long id, Boolean target) {
        if (id == null || target == null) {
            return Response.fail(FALSE_CORE, "挡板开关", null);
        }
        LinkGuardEntity linkGuardEntity = tLinkGuardMapper.selectById(id);
        LinkGuardEntity entity = new LinkGuardEntity();
        entity.setId(id);
        entity.setIsEnable(target ? GuardEnableConstants.GUARD_ENABLE : GuardEnableConstants.GUARD_UNABLE);
        tLinkGuardMapper.update(entity);
        configSyncService.syncGuard(WebPluginUtils.traceTenantCommonExt(), linkGuardEntity.getApplicationId(), null);
        agentConfigCacheManager.evictGuards(linkGuardEntity.getApplicationName());
        return Response.success();
    }

    @Override
    public List<LinkGuardEntity> getAllEnabledGuard(String applicationId) {
        return tLinkGuardMapper.getAllEnabledGuard(applicationId);
    }

    public LinkGuardVo entityToVo(LinkGuardEntity guardEntity) {
        if (guardEntity == null) {
            return null;
        }
        LinkGuardVo vo = new LinkGuardVo();
        vo.setId(guardEntity.getId());
        vo.setApplicationName(guardEntity.getApplicationName());
        vo.setMethodInfo(guardEntity.getMethodInfo());
        vo.setGroovy(guardEntity.getGroovy());
        vo.setCreateTime(guardEntity.getCreateTime());
        vo.setUpdateTime(guardEntity.getUpdateTime());
        vo.setRemark(guardEntity.getRemark());
        if (Objects.nonNull(guardEntity.getIsEnable())) {
            vo.setIsEnable(guardEntity.getIsEnable() == GuardEnableConstants.GUARD_ENABLE);
        }
        // 判断权限，需要把用户传入
        vo.setUserId(guardEntity.getUserId());
        WebPluginUtils.fillQueryResponse(vo);
        return vo;
    }
}
