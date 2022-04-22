package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeChildCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeMainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.RemoteCallConfigCreateRequest;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationRemoteInterfaceConfigService;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.application.InterfaceTypeChildDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeConfigDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeMainDAO;
import io.shulie.takin.web.data.dao.application.MiddlewareTypeDAO;
import io.shulie.takin.web.data.dao.application.RemoteCallConfigDAO;
import io.shulie.takin.web.data.model.mysql.InheritedSelectVO;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ApplicationRemoteInterfaceConfigServiceImpl implements ApplicationRemoteInterfaceConfigService {

    @Resource
    private InterfaceTypeMainDAO interfaceTypeMainDAO;

    @Resource
    private InterfaceTypeChildDAO interfaceTypeChildDAO;

    @Resource
    private RemoteCallConfigDAO remoteCallConfigDAO;

    @Resource
    private InterfaceTypeConfigDAO interfaceTypeConfigDAO;

    @Resource
    private MiddlewareTypeDAO middlewareTypeDAO;

    @Override
    public void addInterfaceTypeMain(InterfaceTypeMainCreateRequest request) {
        request.checkRequired();
        InterfaceTypeMainEntity entity = new InterfaceTypeMainEntity();
        BeanUtils.copyProperties(request, entity);
        // 唯一性校验
        interfaceTypeMainDAO.checkUnique(entity);
        interfaceTypeMainDAO.addEntity(entity);
    }

    @Override
    public void addInterfaceTypeChild(InterfaceTypeChildCreateRequest request) {
        InterfaceTypeChildEntity entity = new InterfaceTypeChildEntity();
        preCheckInterfaceTypeChildAdd(request, entity);
        BeanUtils.copyProperties(request, entity);
        // 唯一性校验
        interfaceTypeChildDAO.checkUnique(entity);
        interfaceTypeChildDAO.addEntity(entity);
    }

    @Override
    public void addRemoteCallConfig(RemoteCallConfigCreateRequest request) {
        request.checkRequired();
        RemoteCallConfigEntity entity = new RemoteCallConfigEntity();
        BeanUtils.copyProperties(request, entity);
        // 唯一性校验
        remoteCallConfigDAO.checkUnique(entity);
        remoteCallConfigDAO.addEntity(entity);

    }

    @Override
    public void addInterfaceTypeConfig(InterfaceTypeConfigCreateRequest request) {
        InterfaceTypeConfigEntity entity = new InterfaceTypeConfigEntity();
        preCheckInterfaceConfigAdd(request, entity);
        BeanUtils.copyProperties(request, entity);
        // 唯一性校验
        interfaceTypeConfigDAO.checkUnique(entity);
        interfaceTypeConfigDAO.addEntity(entity);
    }

    @Override
    public List<InterfaceTypeMainEntity> queryInterfaceTypeMain() {
        return interfaceTypeMainDAO.selectList();
    }

    @Override
    public List<InterfaceTypeChildEntity> queryInterfaceTypeChild() {
        return interfaceTypeChildDAO.selectList();
    }

    @Override
    public List<InterfaceTypeConfigEntity> queryInterfaceTypeConfig() {
        return interfaceTypeConfigDAO.selectList();
    }

    @Override
    public List<RemoteCallConfigEntity> queryRemoteCallConfig() {
        return remoteCallConfigDAO.selectList();
    }

    @Override
    public void deleteInterfaceTypeConfig(Long id) {
        interfaceTypeConfigDAO.deleteById(id);
    }

    @Override
    public List<InheritedSelectVO> queryMiddlewareTypeList() {
        return middlewareTypeDAO.selectList();
    }

    // 前置检查新增 接口子类型
    private void preCheckInterfaceTypeChildAdd(InterfaceTypeChildCreateRequest request, InterfaceTypeChildEntity entity) {
        request.checkRequired();
        String mainName = request.getMainName();
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectByName(mainName);
        if (mainEntity == null) {
            String extra = "";
            Set<String> availableNames = interfaceTypeMainDAO.selectAllAvailableNames();
            if (!CollectionUtils.isEmpty(availableNames)) {
                extra = String.format(", 可配置类型有：[%s]", StringUtils.join(availableNames, ","));
            }
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("中间件主类型[mainName = %s]不存在%s", mainName, extra));
        }
        entity.setMainId(mainEntity.getId());
    }

    // 前置检查新增 接口类型-配置类型
    private void preCheckInterfaceConfigAdd(InterfaceTypeConfigCreateRequest request, InterfaceTypeConfigEntity entity) {
        request.checkRequired();
        String childTypeName = request.getChildTypeName();
        InterfaceTypeChildEntity childEntity = interfaceTypeChildDAO.selectByName(childTypeName);
        if (childEntity == null) {
            String extra = "";
            Set<String> availableNames = interfaceTypeChildDAO.selectAllAvailableNames();
            if (!CollectionUtils.isEmpty(availableNames)) {
                extra = String.format(", 可配置类型有：[%s]", StringUtils.join(availableNames, ","));
            }
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("中间件子类型[childTypeName = %s]不存在%s", childTypeName, extra));
        }
        String configName = request.getConfigName();
        RemoteCallConfigEntity configEntity = remoteCallConfigDAO.selectByName(configName);
        if (configEntity == null) {
            String extra = "";
            Set<String> availableNames = remoteCallConfigDAO.selectAllAvailableNames();
            if (!CollectionUtils.isEmpty(availableNames)) {
                extra = String.format(", 可配置类型有：[%s]", StringUtils.join(availableNames, ","));
            }
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("配置类型[childTypeName = %s]不存在%s", configName, extra));
        }
        entity.setConfigId(configEntity.getId());
        entity.setInterfaceTypeId(childEntity.getId());
    }
}
