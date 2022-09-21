package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.common.CheckStatusEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.IsolateTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.JoinFlagEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.PressureResourceTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.agent.command.*;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.InterfaceTypeMainDAO;
import io.shulie.takin.web.data.dao.application.RemoteCallConfigDAO;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapper;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
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
    private PressureResourceRelateDsMapper resourceDsMapper;
    @Resource
    private PressureResourceRelateTableMapper resourceTableMapper;
    @Resource
    private PressureResourceRelateRemoteCallMapper remoteCallMapper;
    @Resource
    private DictionaryDataDAO dictionaryDataDAO;
    @Resource
    private RemoteCallConfigDAO remoteCallConfigDAO;
    @Resource
    private InterfaceTypeMainDAO interfaceTypeMainDAO;


    /**
     * 下发校验命令并更新数据库
     * @param resourceId
     * @return
     */
    @Override
    public void pushCommand(Long resourceId){
        PressureResourceEntity resource = resourceMapper.selectById(resourceId);
        if(resource == null){
            return;
        }
        //下发数据源校验命令
        pushDataSourceCommands(resource);
        //下发白名单配置
        pushWhitelistConfigs(resource);
    }


    /**
     * 压测数据源命令
     * @param resource
     * @return
     */
    private void pushDataSourceCommands(PressureResourceEntity resource){
        if(resource.getIsolateType() == IsolateTypeEnum.DEFAULT.getCode()){
            //未配置隔离类型
            return;
        }
        List<PressureResourceRelateDsEntity> dsEntities = resourceDsMapper.selectList(new QueryWrapper<PressureResourceRelateDsEntity>().lambda()
                .eq(PressureResourceRelateDsEntity::getResourceId, resource.getId()));
        if(CollectionUtils.isEmpty(dsEntities)){
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
        dsMap.forEach((appName,appDsList) ->{
            List<TakinCommand> collect = appDsList.stream().map(dsEntity -> mapping(resource, dsEntity, tableMap.get(dsEntity.getUniqueKey())))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            list.addAll(collect);
        });
        //下发命令
        String url = joinUrl(agentManagerHost, PUSH_COMMAND_URL);
        String post = HttpUtil.post(url,JSON.toJSONString(list));
        Response<Boolean> response = JSON.parseObject(post, new TypeReference<Response<Boolean>>() {});
        if(response.getData() == null || !response.getData()){
            //未下发成功  下次循环触发
            return;
        }
        //更新数据库
        PressureResourceEntity update = new PressureResourceEntity();
        update.setId(resource.getId());
        update.setStatus(CheckStatusEnum.CHECK_ING.getCode());
        resourceMapper.updateById(update);
    }


    private void pushWhitelistConfigs(PressureResourceEntity resource){
        List<PressureResourceRelateRemoteCallEntity> remoteCallEntities = remoteCallMapper.selectList(new QueryWrapper<PressureResourceRelateRemoteCallEntity>().lambda()
                .eq(PressureResourceRelateRemoteCallEntity::getResourceId, resource.getId()));
        if(CollectionUtils.isEmpty(remoteCallEntities)){
            return;
        }
        //group by appName
        Map<String, List<PressureResourceRelateRemoteCallEntity>> appMap = remoteCallEntities.stream().collect(Collectors.groupingBy(PressureResourceRelateRemoteCallEntity::getAppName));

        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        List<TakinConfig> configList = new ArrayList<>();
        //遍历dsMap
        appMap.forEach((appName,remoteCallList) ->{
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.WHITELIST.getCode());
            List<AgentRemoteCallVO.RemoteCall> collect = remoteCallList.stream().map(this::mapping)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            takinConfig.setConfigParam(JSON.toJSONString(collect));
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url,JSON.toJSONString(configList));
    }


    private AgentRemoteCallVO.RemoteCall mapping(PressureResourceRelateRemoteCallEntity remoteCallEntity){
        if(!StringUtils.hasText(remoteCallEntity.getServerAppName()) || remoteCallEntity.getType() == 0 || remoteCallEntity.getIsDeleted() == 1){
            return null;
        }
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        Map<Integer, RemoteCallConfigEntity> entityMap = remoteCallConfigDAO.selectToMapWithOrderKey();

        AgentRemoteCallVO.RemoteCall remoteCall = new AgentRemoteCallVO.RemoteCall();
        remoteCall.setINTERFACE_NAME(remoteCallEntity.getInterfaceName());
        remoteCall.setTYPE(getSelectVO(remoteCallEntity.getInterfaceType(), voList).getLabel().toLowerCase());
        remoteCall.setCheckType(entityMap.get(remoteCallEntity.getType()).getCheckType());
        if (!StringUtils.hasText(remoteCallEntity.getMockReturnValue())) {
            return null;
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
        switch (ackType){
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
            default:break;

        }


    }

    /**
     * 配置生效响应
     * @param configAck
     */
    private void processConfigAck(TakinConfigAck configAck) {
        long resourceId = Long.parseLong(configAck.getConfigId());
        boolean success = configAck.isSuccess();
        PressureResourceTypeEnum resourceTypeEnum = PressureResourceTypeEnum.getByCode(configAck.getConfigType());
        switch (resourceTypeEnum) {
            case DATABASE:
                String remark = success ? "配置生效" : configAck.getResponse();
                //更新ds 记录失败原因
                List<PressureResourceRelateDsEntity> dsEntities = resourceDsMapper.selectList(new QueryWrapper<PressureResourceRelateDsEntity>().lambda()
                        .eq(PressureResourceRelateDsEntity::getResourceId, resourceId));
                dsEntities.forEach(dsEntity -> {
                    dsEntity.setRemark(dsEntity.getRemark() + ";" + remark);
                    resourceDsMapper.updateById(dsEntity);
                });
                break;
            case WHITELIST:
                break;
            default:break;
        }

    }


    /**
     * 压测配置校验响应处理
     * @param commandAck
     */
    private void processCommandAck(TakinCommandAck commandAck){
        String commandId = commandAck.getCommandId();
        Long resourceId = getResourceId(commandId);
        Long subId = getSubId(commandId);

        PressureResourceEntity resource = resourceMapper.selectById(resourceId);
        if(resource == null){
            return;
        }
        PressureResourceEntity update = new PressureResourceEntity();
        update.setId(resourceId);
        update.setCheckTime(new Date());
        update.setCheckStatus(CheckStatusEnum.CHECK_FIN.getCode());
        resourceMapper.updateById(update);
        //更新附属资源
        PressureResourceTypeEnum resourceTypeEnum = PressureResourceTypeEnum.getByCode(commandAck.getCommandType());
        switch (resourceTypeEnum){
            case DATABASE:
                PressureResourceRelateDsEntity dsEntity = resourceDsMapper.selectById(subId);
                if(dsEntity == null){
                    throw new IllegalArgumentException("未找到对应的数据库资源");
                }
                PressureResourceRelateDsEntity updateDs = new PressureResourceRelateDsEntity();
                updateDs.setId(subId);
                updateDs.setStatus(commandAck.isSuccess() ? 2 : 1);
                updateDs.setRemark(commandAck.isSuccess()? "影子资源连通" : commandAck.getResponse());
                resourceDsMapper.updateById(updateDs);
                //下发数据库配置
                if(commandAck.isSuccess()){
                    pushPressureDatabaseConfig(resource);
                }
                break;
            case WHITELIST:
                break;
            default:
                break;
        }
    }


    /**
     * 校验通过，下发压测库配置
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
        dsMap.forEach((appName,dsList)->{
            TakinConfig takinConfig = new TakinConfig();
            takinConfig.setConfigId(resource.getId().toString());
            takinConfig.setAppName(appName);
            takinConfig.setAgentSpecification(TakinCommand.SIMULATOR_AGENT);
            takinConfig.setEnvCode(resource.getEnvCode());
            takinConfig.setTenantCode(tenantInfoExt.getTenantCode());
            takinConfig.setConfigType(PressureResourceTypeEnum.DATABASE.getCode());
            List<DataSourceConfig> collect = dsList.stream().map(dsEntity -> mapping(resource.getIsolateType(),dsEntity)).collect(Collectors.toList());
            JdbcTableConfig jdbcTableConfig = new JdbcTableConfig();
            jdbcTableConfig.setData(collect);
            takinConfig.setConfigParam(JSON.toJSONString(jdbcTableConfig));
            configList.add(takinConfig);
        });
        //推送配置
        String url = joinUrl(agentManagerHost, PUSH_CONFIG_URL);
        HttpUtil.post(url,JSON.toJSONString(configList));
    }


    private DataSourceConfig mapping(Integer shadowType,PressureResourceRelateDsEntity dsEntity){
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setShadowType(shadowType);
        dataSourceConfig.setUrl(dsEntity.getBusinessDatabase());
        dataSourceConfig.setUsername(dsEntity.getBusinessUserName());
        dataSourceConfig.setShadowUrl(dsEntity.getShadowDatabase());
        dataSourceConfig.setShadowUsername(dsEntity.getShadowUserName());
        dataSourceConfig.setShadowPassword(dsEntity.getShadowPassword());
        if(!shadowType.equals(IsolateTypeEnum.SHADOW_TABLE.getCode())){
            //非影子表模式 无表配置
            return dataSourceConfig;
        }
        List<PressureResourceRelateTableEntity> tableEntities = resourceTableMapper.selectList(new QueryWrapper<PressureResourceRelateTableEntity>().lambda()
                .eq(PressureResourceRelateTableEntity::getResourceId, dsEntity.getResourceId())
                .eq(PressureResourceRelateTableEntity::getJoinFlag, JoinFlagEnum.YES.getCode()));
        if(CollectionUtils.isEmpty(tableEntities)){
            dataSourceConfig.setDisabled(true);
            return dataSourceConfig;
        }
        List<String> bizTables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
        dataSourceConfig.setBizTables(bizTables);
        return dataSourceConfig;
    }


    private TakinCommand mapping(PressureResourceEntity resource,PressureResourceRelateDsEntity dsEntity,List<PressureResourceRelateTableEntity> tableEntities){
        if(!StringUtils.hasText(dsEntity.getBusinessDatabase()) || !StringUtils.hasText(dsEntity.getBusinessUserName()) || !StringUtils.hasText(dsEntity.getAppName())){
            return null;
        }
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        if(IsolateTypeEnum.SHADOW_TABLE.getCode() ==  resource.getIsolateType() && CollectionUtils.isEmpty(tableEntities)){
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
        if(!CollectionUtils.isEmpty(tableEntities)){
            List<String> tables = tableEntities.stream().map(PressureResourceRelateTableEntity::getBusinessTable).collect(Collectors.toList());
            tableCompareCommand.setTables(tables);
        }
        //影子库
        if(StringUtils.hasText(dsEntity.getShadowDatabase())){
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



    private String commandId(Long resourceId,Long subId){
        return resourceId+"_"+subId;
    }


    private Long getResourceId(String commandId){
        String[] split = commandId.split("_");
        if(split.length != 2){
            throw new IllegalArgumentException("命令id校验失败:"+commandId);
        }
        return Long.parseLong(split[0]);
    }

    private Long getSubId(String commandId){
        String[] split = commandId.split("_");
        if(split.length != 2){
            throw new IllegalArgumentException("命令id校验失败:"+commandId);
        }
        return Long.parseLong(split[1]);
    }




}
