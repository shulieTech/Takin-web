package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.job.ResourceContextUtil;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.common.*;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.biz.service.pressureresource.vo.agent.command.*;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.secure.SecureUtil;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsManageDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeMainDAO;
import io.shulie.takin.web.data.dao.application.RemoteCallConfigDAO;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.*;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.*;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guann1n9
 * @date 2022/9/14 10:57 AM
 */
@Service
@Slf4j
public class PressureResourceCommandServiceImpl implements PressureResourceCommandService {

    private static final String PUSH_COMMAND_URL = "takin/command";

    private static final String PUSH_CONFIG_URL = "takin/config";

    @Value("${agent.manager.host}")
    private String agentManagerHost;

    @Resource
    private PressureResourceMapper resourceMapper;
    @Resource
    private PressureResourceRelateDsMapperV2 resourceRelateDsMapperV2;

    @Resource
    private PressureResourceRelateDsMapper resourceDsMapper;

    @Resource
    private PressureResourceRelateTableMapper resourceTableMapper;

    @Resource
    private PressureResourceRelateTableMapperV2 resourceRelateTableMapperV2;
    @Resource
    private PressureResourceRelateRemoteCallMapper remoteCallMapper;
    @Resource
    private PressureResourceRelateMqConsumerMapper mqConsumerMapper;

    @Resource
    private DictionaryDataDAO dictionaryDataDAO;
    @Resource
    private RemoteCallConfigDAO remoteCallConfigDAO;
    @Resource
    private InterfaceTypeMainDAO interfaceTypeMainDAO;
    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;
    @Resource
    private ApplicationDsManageDAO applicationDsManageDAO;

    @Resource
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;
    @Resource
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    /**
     * 下发校验命令并更新数据库
     *
     * @param taskVo
     * @return
     */
    @Override
    public void pushCommand(CommandTaskVo taskVo) {
        PressureResourceEntity resource = resourceMapper.selectById(taskVo.getResourceId());
        if (resource == null) {
            return;
        }
        //下发数据源校验命令，校验通过后，再下发配置
        if (taskVo.getModule().equals(ModuleEnum.DS.getCode()) || taskVo.getModule().equals(ModuleEnum.ALL.getCode())) {
            pushDataSourceCommands_v2(resource);
        }
        if (taskVo.getModule().equals(ModuleEnum.REMOTECALL.getCode()) || taskVo.getModule().equals(ModuleEnum.ALL.getCode())) {
            //下发白名单配置，无需校验
            pushWhitelistConfigs(resource);
        }
        if (taskVo.getModule().equals(ModuleEnum.MQ.getCode()) || taskVo.getModule().equals(ModuleEnum.ALL.getCode())) {
            //下发mq验证
            pushMqCommands(resource);
        }
    }


    /**
     * mq压测配置校验命令下发
     *
     * @param resource
     */
    private void pushMqCommands(PressureResourceEntity resource) {
        List<PressureResourceRelateMqConsumerEntity> mqConsumerEntities = mqConsumerMapper.selectList(new QueryWrapper<PressureResourceRelateMqConsumerEntity>().lambda()
                .eq(PressureResourceRelateMqConsumerEntity::getResourceId, resource.getId()));
        if (CollectionUtils.isEmpty(mqConsumerEntities)) {
            return;
        }

        List<Long> dsManageIds = mqConsumerEntities.stream().filter(entity -> entity.getRelateDsManageId() != null).map(entity -> entity.getRelateDsManageId()).collect(Collectors.toList());
        if (!dsManageIds.isEmpty()) {
            List<ApplicationDsManageEntity> manageEntities = applicationDsManageDAO.listByIds(dsManageIds);
            Map<Long, ApplicationDsManageEntity> mappings = new HashMap<>();
            manageEntities.forEach(applicationDsManageEntity -> mappings.put(applicationDsManageEntity.getId(), applicationDsManageEntity));

            // 把application_ds_manage的属性填充到配置上
            mqConsumerEntities.stream().forEach(entity -> {
                if (entity.getRelateDsManageId() != null) {
                    populateKafkaClusterProperties(entity, mappings.get(entity.getRelateDsManageId()));
                }
            });
        }

        //租户信息
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        //验证命令
        List<TakinCommand> commandList = mqConsumerEntities.stream().filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getApplicationName()))
                .filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getMqType()))
                .filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getTopic()))
                .map(mqConsumerEntity -> mqResourceToCommand(resource, mqConsumerEntity, tenantInfoExt))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(commandList)) {
            return;
        }
        //下发命令
        boolean succ = sendCommand(commandList);
        if (succ) {
            pushCommandSuccess(resource.getId());
        }
    }

    /**
     * command参数转换
     *
     * @return
     */
    private TakinCommand mqResourceToCommand(PressureResourceEntity resource, PressureResourceRelateMqConsumerEntity mqConsumerEntity, TenantInfoExt tenantInfoExt) {
        TakinCommand takinCommand = new TakinCommand();
        takinCommand.setCommandId(commandId(resource.getId(), mqConsumerEntity.getId()));
        takinCommand.setAppName(mqConsumerEntity.getApplicationName());
        takinCommand.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
        takinCommand.setEnvCode(resource.getEnvCode());
        takinCommand.setTenantCode(tenantInfoExt.getTenantCode());
        takinCommand.setCommandType(PressureResourceTypeEnum.MQ.getCode());
        Object config = mqResourceConfig(mqConsumerEntity);
        if (Objects.isNull(config)) {
            return null;
        }
        takinCommand.setCommandParam(JSON.toJSONString(Collections.singletonList(config)));
        return takinCommand;
    }

    /**
     * 压测数据源命令
     *
     * @param resource
     * @return
     */
    private void pushDataSourceCommands_v2(PressureResourceEntity resource) {
        if (resource.getIsolateType() == IsolateTypeEnum.DEFAULT.getCode()) {
            //未配置隔离类型
            return;
        }
        // 查询关联的数据源信息
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(resource.getId());
        List<PressureResourceRelateDsEntity> dsEntities = pressureResourceRelateDsDAO.queryByParam_v2(dsQueryParam);
        if (CollectionUtils.isEmpty(dsEntities)) {
            return;
        }
        // 查询tables
        PressureResourceTableQueryParam tableQueryParam = new PressureResourceTableQueryParam();
        tableQueryParam.setResourceId(resource.getId());
        List<PressureResourceRelateTableEntity> tmpList = pressureResourceRelateTableDAO.queryList_v2(tableQueryParam);
        List<PressureResourceRelateTableEntity> tableEntities = tmpList.stream().filter(tmp -> tmp.getJoinFlag() == JoinFlagEnum.YES.getCode()).collect(Collectors.toList());

        //appName分组
        Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateDsEntity::getAppName));
        //dsKey分组
        Map<String, List<PressureResourceRelateTableEntity>> tableMap = tableEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateTableEntity::getDsKey));

        List<TakinCommand> list = new ArrayList<>();
        //遍历dsMap
        dsMap.forEach((appName, appDsList) -> {
            List<TakinCommand> collect = appDsList.stream().map(dsEntity -> {
                String dsKey = DataSourceUtil.generateDsKey_ext(dsEntity.getBusinessDatabase(), dsEntity.getBusinessUserName());
                // 找到数据源和对应的表信息
                return mapping_v2(resource, dsEntity, tableMap.get(dsKey));
            }).filter(Objects::nonNull).collect(Collectors.toList());
            list.addAll(collect);
        });
        //下发命令
        boolean succ = sendCommand(list);
        if (succ) {
            pushCommandSuccess(resource.getId());
        }
    }

    /**
     * 压测数据源命令
     *
     * @param resource
     * @return
     */
    private void pushDataSourceCommands(PressureResourceEntity resource) {
        if (resource.getIsolateType() == IsolateTypeEnum.DEFAULT.getCode()) {
            //未配置隔离类型
            return;
        }
        List<PressureResourceRelateDsEntity> dsEntities = resourceDsMapper.selectList(new QueryWrapper<PressureResourceRelateDsEntity>().lambda()
                .eq(PressureResourceRelateDsEntity::getResourceId, resource.getId()));
        if (CollectionUtils.isEmpty(dsEntities)) {
            return;
        }
        List<PressureResourceRelateTableEntity> tableEntities = resourceTableMapper.selectList(new QueryWrapper<PressureResourceRelateTableEntity>().lambda()
                .eq(PressureResourceRelateTableEntity::getResourceId, resource.getId())
                .eq(PressureResourceRelateTableEntity::getJoinFlag, JoinFlagEnum.YES.getCode()));
        //appName分组
        Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateDsEntity::getAppName));
        //dsKey分组
        Map<String, List<PressureResourceRelateTableEntity>> tableMap = tableEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateTableEntity::getDsKey));

        List<TakinCommand> list = new ArrayList<>();
        //遍历dsMap
        dsMap.forEach((appName, appDsList) -> {
            List<TakinCommand> collect = appDsList.stream().map(dsEntity -> {
                String dsKey = DataSourceUtil.generateDsKey(dsEntity.getResourceId(), dsEntity.getBusinessDatabase());
                return mapping(resource, dsEntity, tableMap.get(dsKey));
            }).filter(Objects::nonNull).collect(Collectors.toList());
            list.addAll(collect);
        });
        //下发命令
        boolean succ = sendCommand(list);
        if (succ) {
            pushCommandSuccess(resource.getId());
        }
    }


    private boolean sendCommand(List<TakinCommand> commandList) {
        //下发命令
        String url = joinUrl(agentManagerHost, PUSH_COMMAND_URL);
        String post = HttpUtil.post(url, JSON.toJSONString(commandList));
        Response<Boolean> response = JSON.parseObject(post, new TypeReference<Response<Boolean>>() {
        });
        if (response.getData() == null || !response.getData()) {
            //未下发成功  下次循环触发
            return false;
        }
        return true;
    }


    private void pushCommandSuccess(Long resourceId) {
        //更新数据库
        PressureResourceEntity update = new PressureResourceEntity();
        update.setId(resourceId);
        update.setCheckStatus(CheckStatusEnum.CHECK_ING.getCode());
        resourceMapper.updateById(update);
    }


    private void pushWhitelistConfigs(PressureResourceEntity resource) {
        List<PressureResourceRelateRemoteCallEntity> remoteCallEntities = remoteCallMapper.selectList(new QueryWrapper<PressureResourceRelateRemoteCallEntity>().lambda()
                .eq(PressureResourceRelateRemoteCallEntity::getResourceId, resource.getId()));
        if (CollectionUtils.isEmpty(remoteCallEntities)) {
            return;
        }

        // 查询旧表数据
        Set<Long> appRemoteCallIds = remoteCallEntities.stream().map(entity -> entity.getRelateAppRemoteCallId()).collect(Collectors.toSet());
        if (!appRemoteCallIds.isEmpty()) {
            List<AppRemoteCallEntity> appRemoteCallEntities = appRemoteCallDAO.listByIds(appRemoteCallIds);
            Map<Long, AppRemoteCallEntity> mappings = new HashMap<>();
            for (AppRemoteCallEntity callEntity : appRemoteCallEntities) {
                mappings.put(callEntity.getId(), callEntity);
            }
            remoteCallEntities.forEach(entity -> populateRemoteCallProperties(entity, mappings.get(entity.getRelateAppRemoteCallId())));
        }

        //group by appName
        Map<String, List<PressureResourceRelateRemoteCallEntity>> appMap = remoteCallEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateRemoteCallEntity::getAppName));

        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        List<TakinConfig> configList = new ArrayList<>();
        //遍历dsMap
        appMap.forEach((appName, remoteCallList) -> {
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.WHITELIST.getCode());
            List<AgentRemoteCallVO.RemoteCall> collect = remoteCallList.stream()
                    .filter(remoteCallEntity -> !AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType().equals(remoteCallEntity.getType()))
                    .map(this::mapping)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            String configParam = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                configParam = mapper.writeValueAsString(collect);
            } catch (JsonProcessingException e) {
            }
            takinConfig.setConfigParam(configParam);
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url, JSON.toJSONString(configList));
    }


    private AgentRemoteCallVO.RemoteCall mapping(PressureResourceRelateRemoteCallEntity remoteCallEntity) {
        if (!StringUtils.hasText(remoteCallEntity.getServerAppName()) || remoteCallEntity.getType() == 0 || remoteCallEntity.getIsDeleted() == 1) {
            return null;
        }
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        Map<Integer, RemoteCallConfigEntity> entityMap = remoteCallConfigDAO.selectToMapWithOrderKey();

        AgentRemoteCallVO.RemoteCall remoteCall = new AgentRemoteCallVO.RemoteCall();
        remoteCall.setINTERFACE_NAME(remoteCallEntity.getInterfaceName());
        remoteCall.setTYPE(getSelectVO(remoteCallEntity.getInterfaceType(), voList).getLabel().toLowerCase());
        remoteCall.setCheckType(entityMap.get(remoteCallEntity.getType()).getCheckType());
        if (!StringUtils.hasText(remoteCallEntity.getMockReturnValue())) {
            return remoteCall;
        }
        MockInfo mockInfo = JSON.parseObject(remoteCallEntity.getMockReturnValue(), MockInfo.class);
        remoteCall.setContent(mockInfo.getMockValue());
        return remoteCall;
    }

    private SelectVO getSelectVO(Integer interfaceType, List<TDictionaryVo> voList) {
        InterfaceTypeMainEntity mainEntity = interfaceTypeMainDAO.selectByOrder(interfaceType);
        if (mainEntity == null) {
            String type = String.valueOf(interfaceType);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(voList)) {
                return new SelectVO("数据字典未找到类型", type);
            }
            List<TDictionaryVo> dictionaryVoList = voList.stream().filter(t -> type.equals(t.getValueCode())).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(dictionaryVoList)) {
                return new SelectVO("数据字典未找到类型", type);
            }
            TDictionaryVo vos = dictionaryVoList.get(0);
            return new SelectVO(vos.getValueName(), type);
        } else {
            return new SelectVO(mainEntity.getName(), String.valueOf(mainEntity.getValueOrder()));
        }
    }


    @Override
    public void processAck(TakinAck takinAck) {

        String ackType = takinAck.getAckType();
        switch (ackType) {
            case TakinAck.COMMAND:
                //配置校验响应
                TakinCommandAck takinCommandAck = JSON.parseObject(JSON.toJSONString(takinAck.getAck()), TakinCommandAck.class);
                processCommandAck(takinCommandAck);
                break;
            case TakinAck.CONFIG:
                //配置生效响应
                TakinConfigAck takinConfigAck = JSON.parseObject(JSON.toJSONString(takinAck.getAck()), TakinConfigAck.class);
                processConfigAck(takinConfigAck);
                break;
            default:
                break;

        }


    }

    /**
     * 配置生效响应
     *
     * @param configAck
     */
    private void processConfigAck(TakinConfigAck configAck) {
        long resourceId = Long.parseLong(configAck.getConfigId());
        PressureResourceEntity resource = resourceMapper.queryByIdNoTenant(resourceId);
        if (resource == null) {
            return;
        }
        //设置租户上下文
        ResourceContextUtil.setTenantContext(resource);
        boolean success = configAck.isSuccess();
        PressureResourceTypeEnum resourceTypeEnum = PressureResourceTypeEnum.getByCode(configAck.getConfigType());
        switch (resourceTypeEnum) {
            case DATABASE:
                String dsResponse = success ? "配置生效" : configAck.getResponse();
                //更新ds 记录失败原因
                List<PressureResourceRelateDsEntity> dsEntities = resourceDsMapper.selectList(new QueryWrapper<PressureResourceRelateDsEntity>().lambda()
                        .eq(PressureResourceRelateDsEntity::getResourceId, resourceId));
                dsEntities.forEach(dsEntity -> {
                    dsEntity.setRemark(dsEntity.getRemark() + ";" + dsResponse);
                    resourceDsMapper.updateById(dsEntity);
                });
                break;
            case MQ:
                String mqResponse = success ? "配置生效" : configAck.getResponse();
                //更新ds 记录失败原因
                List<PressureResourceRelateMqConsumerEntity> mqConsumerEntities = mqConsumerMapper.selectList(new QueryWrapper<PressureResourceRelateMqConsumerEntity>().lambda()
                        .eq(PressureResourceRelateMqConsumerEntity::getResourceId, resourceId));
                mqConsumerEntities.forEach(mqEntity -> {
                    mqEntity.setRemark(mqEntity.getRemark() + ";" + mqResponse);
                    mqConsumerMapper.updateById(mqEntity);
                });
                break;
            default:
                break;
        }

    }

    /**
     * 压测配置校验响应处理
     *
     * @param commandAck
     */
    private void processCommandAck(TakinCommandAck commandAck) {
        String commandId = commandAck.getCommandId();
        Long resourceId = getResourceId(commandId);
        Long subId = getSubId(commandId);

        PressureResourceEntity resource = resourceMapper.queryByIdNoTenant(resourceId);
        if (resource == null) {
            return;
        }
        ResourceContextUtil.setTenantContext(resource);

        PressureResourceEntity update = new PressureResourceEntity();
        update.setId(resourceId);
        update.setCheckTime(new Date());
        update.setCheckStatus(CheckStatusEnum.CHECK_FIN.getCode());
        resourceMapper.updateById(update);
        //更新附属资源
        PressureResourceTypeEnum resourceTypeEnum = PressureResourceTypeEnum.getByCode(commandAck.getCommandType());
        switch (resourceTypeEnum) {
            case DATABASE:
                //更新ds表
                PressureResourceRelateDsEntityV2 dsEntity = resourceRelateDsMapperV2.selectById(subId);
                if (dsEntity == null) {
                    throw new IllegalArgumentException("未找到对应的数据库资源");
                }
                PressureResourceRelateDsEntityV2 updateDs = new PressureResourceRelateDsEntityV2();
                updateDs.setId(subId);
                updateDs.setStatus(commandAck.isSuccess() ? 2 : 1);
                updateDs.setRemark(commandAck.isSuccess() ? "影子资源连通" : commandAck.getResponse());
                resourceRelateDsMapperV2.updateById(updateDs);

                //更新table表
                String dsKey = DataSourceUtil.generateDsKey_ext(dsEntity.getBusinessDatabase(), dsEntity.getBusinessUserName());
                // 查询tables
                PressureResourceTableQueryParam tableQueryParam = new PressureResourceTableQueryParam();
                tableQueryParam.setResourceId(resource.getId());
                tableQueryParam.setDsKey(dsKey);
                List<PressureResourceRelateTableEntity> tmpList = pressureResourceRelateTableDAO.queryList_v2(tableQueryParam);
                List<PressureResourceRelateTableEntity> tableEntities = tmpList.stream().filter(tmp -> tmp.getJoinFlag() == JoinFlagEnum.YES.getCode()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(tableEntities)) {
                    tableEntities.forEach(table -> {
                        PressureResourceRelateTableEntityV2 v2 = new PressureResourceRelateTableEntityV2();
                        v2.setId(table.getId());
                        v2.setStatus(commandAck.isSuccess() ? 2 : 1);
                        v2.setRemark(commandAck.isSuccess() ? "影子表正确" : commandAck.getResponse());
                        resourceRelateTableMapperV2.updateById(v2);
                    });
                }
                //下发数据库配置
                if (commandAck.isSuccess()) {
                    pushPressureDatabaseConfig_v2(resource);
                }
                break;
            case MQ:
                PressureResourceRelateMqConsumerEntity consumerEntity = mqConsumerMapper.selectById(subId);
                if (consumerEntity == null) {
                    throw new IllegalArgumentException("未找到对应的MQ资源");
                }
                PressureResourceRelateMqConsumerEntity updateMq = new PressureResourceRelateMqConsumerEntity();
                updateMq.setId(subId);
                updateMq.setStatus(commandAck.isSuccess() ? 2 : 1);
                updateMq.setRemark(commandAck.isSuccess() ? "MQ资源配置正确" : commandAck.getResponse());
                mqConsumerMapper.updateById(updateMq);
                //下发数据库配置
                if (commandAck.isSuccess()) {
                    pushPressureMqConfig(resource);
                }
                break;
            default:
                break;
        }
    }

    private void pushPressureMqConfig(PressureResourceEntity resource) {
        //租户信息
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());

        List<PressureResourceRelateMqConsumerEntity> mqConsumerEntities = mqConsumerMapper.selectList(new QueryWrapper<PressureResourceRelateMqConsumerEntity>().lambda()
                .eq(PressureResourceRelateMqConsumerEntity::getResourceId, resource.getId()));
        List<PressureResourceRelateMqConsumerEntity> validRecords = mqConsumerEntities.stream().filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getApplicationName()))
                .filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getMqType()))
                .filter(mqConsumerEntity -> StringUtils.hasText(mqConsumerEntity.getTopic())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validRecords)) {
            return;
        }
        //按照应用名称分组
        Map<String, List<PressureResourceRelateMqConsumerEntity>> appMap = validRecords.stream().collect(Collectors.groupingBy(PressureResourceRelateMqConsumerEntity::getApplicationName));
        List<TakinConfig> configList = new ArrayList<>();
        appMap.forEach((appName, mqList) -> {
            List<Object> collect = mqList.stream().map(this::mqResourceConfig).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                return;
            }
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.MQ.getCode());
            takinConfig.setConfigParam(JSON.toJSONString(collect));
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url, JSON.toJSONString(configList));
    }


    private Object mqResourceConfig(PressureResourceRelateMqConsumerEntity mqConsumerEntity) {
        MqTypeEnum mqTypeEnum = MqTypeEnum.getByCode(mqConsumerEntity.getMqType());
        switch (mqTypeEnum) {
            case SF_KAKFA:
                return SfKakfaConfig.mapping(mqConsumerEntity);
            default:
                return null;
        }
    }

    /**
     * 校验通过，下发压测库配置
     *
     * @param resource
     */
    private void pushPressureDatabaseConfig_v2(PressureResourceEntity resource) {
        //租户信息
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());

        // 查询Resource
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(resource.getId());
        List<PressureResourceRelateDsEntity> dsEntities = pressureResourceRelateDsDAO.queryByParam_v2(dsQueryParam);
        if (CollectionUtils.isEmpty(dsEntities)) {
            return;
        }
        //appName分组
        Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateDsEntity::getAppName));
        List<TakinConfig> configList = new ArrayList<>();
        dsMap.forEach((appName, dsList) -> {
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.DATABASE.getCode());
            List<DataSourceConfig> collect = dsList.stream().map(dsEntity -> mapping_v2(resource.getIsolateType(), dsEntity)).collect(Collectors.toList());
            JdbcTableConfig jdbcTableConfig = new JdbcTableConfig();
            jdbcTableConfig.setData(collect);
            takinConfig.setConfigParam(JSON.toJSONString(jdbcTableConfig));
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url, JSON.toJSONString(configList));
    }

    /**
     * 校验通过，下发压测库配置
     *
     * @param resource
     */
    private void pushPressureDatabaseConfig(PressureResourceEntity resource) {
        //租户信息
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());

        List<PressureResourceRelateDsEntity> dsEntities = resourceDsMapper.selectList(new QueryWrapper<PressureResourceRelateDsEntity>().lambda()
                .eq(PressureResourceRelateDsEntity::getResourceId, resource.getId()));
        //appName分组
        Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateDsEntity::getAppName));
        List<TakinConfig> configList = new ArrayList<>();
        dsMap.forEach((appName, dsList) -> {
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.DATABASE.getCode());
            List<DataSourceConfig> collect = dsList.stream().map(dsEntity -> mapping(resource.getIsolateType(), dsEntity)).collect(Collectors.toList());
            JdbcTableConfig jdbcTableConfig = new JdbcTableConfig();
            jdbcTableConfig.setData(collect);
            takinConfig.setConfigParam(JSON.toJSONString(jdbcTableConfig));
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url, JSON.toJSONString(configList));
    }

    private DataSourceConfig mapping_v2(Integer shadowType, PressureResourceRelateDsEntity dsEntity) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setShadowType(shadowType);
        dataSourceConfig.setUrl(dsEntity.getBusinessDatabase());
        dataSourceConfig.setUsername(dsEntity.getBusinessUserName());
        dataSourceConfig.setShadowUrl(dsEntity.getShadowDatabase());
        dataSourceConfig.setShadowUsername(dsEntity.getShadowUserName());
        dataSourceConfig.setShadowPassword(dsEntity.getShadowPassword());
        if (!shadowType.equals(IsolateTypeEnum.SHADOW_TABLE.getCode())) {
            //非影子表模式 无表配置
            return dataSourceConfig;
        }
        String dsKey = DataSourceUtil.generateDsKey_ext(dsEntity.getBusinessDatabase(), dsEntity.getBusinessUserName());
        // 查询tables
        PressureResourceTableQueryParam tableQueryParam = new PressureResourceTableQueryParam();
        tableQueryParam.setResourceId(dsEntity.getResourceId());
        tableQueryParam.setDsKey(dsKey);
        List<PressureResourceRelateTableEntity> tmpList = pressureResourceRelateTableDAO.queryList_v2(tableQueryParam);
        List<PressureResourceRelateTableEntity> tableEntities = tmpList.stream().filter(tmp -> tmp.getJoinFlag() == JoinFlagEnum.YES.getCode()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tableEntities)) {
            tableEntities = tableEntities.stream().filter(it -> it.getJoinFlag() == JoinFlagEnum.YES.getCode()).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(tableEntities)) {
            dataSourceConfig.setDisabled(true);
            return dataSourceConfig;
        }
        List<String> bizTables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
        dataSourceConfig.setBizTables(bizTables);
        return dataSourceConfig;
    }

    private DataSourceConfig mapping(Integer shadowType, PressureResourceRelateDsEntity dsEntity) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setShadowType(shadowType);
        dataSourceConfig.setUrl(dsEntity.getBusinessDatabase());
        dataSourceConfig.setUsername(dsEntity.getBusinessUserName());
        dataSourceConfig.setShadowUrl(dsEntity.getShadowDatabase());
        dataSourceConfig.setShadowUsername(dsEntity.getShadowUserName());
        dataSourceConfig.setShadowPassword(dsEntity.getShadowPassword());
        if (!shadowType.equals(IsolateTypeEnum.SHADOW_TABLE.getCode())) {
            //非影子表模式 无表配置
            return dataSourceConfig;
        }
        String dsKey = DataSourceUtil.generateDsKey(dsEntity.getResourceId(), dsEntity.getBusinessDatabase());
        List<PressureResourceRelateTableEntity> tableEntities = resourceTableMapper.selectList(new QueryWrapper<PressureResourceRelateTableEntity>().lambda()
                .eq(PressureResourceRelateTableEntity::getDsKey, dsKey)
                .eq(PressureResourceRelateTableEntity::getJoinFlag, JoinFlagEnum.YES.getCode()));
        if (CollectionUtils.isEmpty(tableEntities)) {
            dataSourceConfig.setDisabled(true);
            return dataSourceConfig;
        }
        List<String> bizTables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
        dataSourceConfig.setBizTables(bizTables);
        return dataSourceConfig;
    }


    private TakinCommand mapping_v2(PressureResourceEntity resource, PressureResourceRelateDsEntity dsEntity, List<PressureResourceRelateTableEntity> tableEntities) {
        if (!StringUtils.hasText(dsEntity.getBusinessDatabase())
                || !StringUtils.hasText(dsEntity.getBusinessUserName())
                || !StringUtils.hasText(dsEntity.getAppName())) {
            return null;
        }
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        if (IsolateTypeEnum.SHADOW_TABLE.getCode() == resource.getIsolateType() && CollectionUtils.isEmpty(tableEntities)) {
            //影子表模式无影子表 不再下发校验命令 推送禁用配置
            pushPressureDatabaseConfig_v2(resource);
            return null;
        }
        TakinCommand takinCommand = new TakinCommand();
        takinCommand.setCommandId(commandId(resource.getId(), dsEntity.getId()));
        takinCommand.setAppName(dsEntity.getAppName());
        takinCommand.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
        takinCommand.setEnvCode(resource.getEnvCode());
        takinCommand.setTenantCode(tenantInfoExt.getTenantCode());
        takinCommand.setCommandType(PressureResourceTypeEnum.DATABASE.getCode());
        //命令
        JdbcTableCompareCommand tableCompareCommand = new JdbcTableCompareCommand();
        tableCompareCommand.setShadowType(resource.getIsolateType());
        DataSourceEntity bizDataSource = new DataSourceEntity();
        bizDataSource.setUrl(dsEntity.getBusinessDatabase());
        bizDataSource.setUserName(dsEntity.getBusinessUserName());
        tableCompareCommand.setBizDataSource(bizDataSource);
        //影子表
        if (!CollectionUtils.isEmpty(tableEntities)) {
            List<String> tables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
            tableCompareCommand.setTables(tables);
        }
        //影子库
        if (StringUtils.hasText(dsEntity.getShadowDatabase())) {
            DataSourceEntity shadowDataSource = new DataSourceEntity();
            shadowDataSource.setUrl(dsEntity.getShadowDatabase());
            shadowDataSource.setUserName(dsEntity.getShadowUserName());
            shadowDataSource.setPassword(dsEntity.getShadowPassword());
            tableCompareCommand.setShadowDataSource(shadowDataSource);
        }
        takinCommand.setCommandParam(JSON.toJSONString(tableCompareCommand));
        return takinCommand;
    }

    private TakinCommand mapping(PressureResourceEntity resource, PressureResourceRelateDsEntity dsEntity, List<PressureResourceRelateTableEntity> tableEntities) {
        if (!StringUtils.hasText(dsEntity.getBusinessDatabase()) || !StringUtils.hasText(dsEntity.getBusinessUserName()) || !StringUtils.hasText(dsEntity.getAppName())) {
            return null;
        }
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        if (IsolateTypeEnum.SHADOW_TABLE.getCode() == resource.getIsolateType() && CollectionUtils.isEmpty(tableEntities)) {
            //影子表模式无影子表 不再下发校验命令 推送禁用配置
            pushPressureDatabaseConfig(resource);
            return null;
        }
        TakinCommand takinCommand = new TakinCommand();
        takinCommand.setCommandId(commandId(resource.getId(), dsEntity.getId()));
        takinCommand.setAppName(dsEntity.getAppName());
        takinCommand.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
        takinCommand.setEnvCode(resource.getEnvCode());
        takinCommand.setTenantCode(tenantInfoExt.getTenantCode());
        takinCommand.setCommandType(PressureResourceTypeEnum.DATABASE.getCode());
        //命令
        JdbcTableCompareCommand tableCompareCommand = new JdbcTableCompareCommand();
        tableCompareCommand.setShadowType(resource.getIsolateType());
        DataSourceEntity bizDataSource = new DataSourceEntity();
        bizDataSource.setUrl(dsEntity.getBusinessDatabase());
        bizDataSource.setUserName(dsEntity.getBusinessUserName());
        tableCompareCommand.setBizDataSource(bizDataSource);
        //影子表
        if (!CollectionUtils.isEmpty(tableEntities)) {
            List<String> tables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
            tableCompareCommand.setTables(tables);
        }
        //影子库
        if (StringUtils.hasText(dsEntity.getShadowDatabase())) {
            DataSourceEntity shadowDataSource = new DataSourceEntity();
            shadowDataSource.setUrl(dsEntity.getShadowDatabase());
            shadowDataSource.setUserName(dsEntity.getShadowUserName());
            shadowDataSource.setPassword(dsEntity.getShadowPassword());
            tableCompareCommand.setShadowDataSource(shadowDataSource);
        }
        takinCommand.setCommandParam(JSON.toJSONString(tableCompareCommand));
        return takinCommand;
    }


    private String joinUrl(String host, String path) {
        return host.endsWith("/") ? host + path : host + "/" + path;
    }


    private String commandId(Long resourceId, Long subId) {
        return resourceId + "_" + subId;
    }


    private Long getResourceId(String commandId) {
        String[] split = commandId.split("_");
        if (split.length != 2) {
            throw new IllegalArgumentException("命令id校验失败:" + commandId);
        }
        return Long.parseLong(split[0]);
    }

    private Long getSubId(String commandId) {
        String[] split = commandId.split("_");
        if (split.length != 2) {
            throw new IllegalArgumentException("命令id校验失败:" + commandId);
        }
        return Long.parseLong(split[1]);
    }

    private void populateRemoteCallProperties(PressureResourceRelateRemoteCallEntity entity, AppRemoteCallEntity appRemoteCall) {
        if (appRemoteCall == null) {
            return;
        }
        entity.setInterfaceName(appRemoteCall.getInterfaceName());
        entity.setInterfaceType(appRemoteCall.getInterfaceType());
        entity.setRemark(appRemoteCall.getRemark());
        entity.setType(appRemoteCall.getType());
        entity.setMockReturnValue(appRemoteCall.getMockReturnValue());
        entity.setUserId(appRemoteCall.getUserId());
        entity.setIsSynchronize(appRemoteCall.getIsSynchronize() == null ? 0 : appRemoteCall.getIsSynchronize() ? 1 : 0);
    }

    private void populateKafkaClusterProperties(PressureResourceRelateMqConsumerEntity consumer, ApplicationDsManageEntity entity) {
        if (entity == null) {
            return;
        }
        JSONObject object = JSON.parseObject(SecureUtil.decrypt(entity.getParseConfig()));
        consumer.setTopic(object.getString("topic"));
        consumer.setBrokerAddr(object.getString("brokerAddr"));
        consumer.setGroup(object.getString("group"));
        consumer.setSystemIdToken(object.getString("systemIdToken"));
        consumer.setTopicTokens(object.getString("topicTokens"));

        Map<String, Object> feature = new HashMap<>();
        feature.put("clusterName", object.getString("clusterName"));
        feature.put("clusterAddr", object.getString("monitorUrl"));
        feature.put("providerThreadCount", object.getInteger("poolSize"));
        feature.put("messageConsumeThreadCount", object.getInteger("messageConsumeThreadCount"));
        consumer.setFeature(JSON.toJSONString(feature));
    }


}
