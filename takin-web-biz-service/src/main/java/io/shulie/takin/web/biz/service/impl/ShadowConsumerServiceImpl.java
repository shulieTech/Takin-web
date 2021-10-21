package io.shulie.takin.web.biz.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pamirs.takin.common.enums.ds.MQTypeEnum;
import com.pamirs.takin.common.util.MD5Util;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.enums.MiddlewareTypeGroupEnum;
import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateUserInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumersOperateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumersOperateInput.ShadowConsumerOperateInput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowMqConsumerOutput;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import io.shulie.takin.web.common.constant.ShadowConsumerConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.MqConfigTemplateDAO;
import io.shulie.takin.web.data.dao.application.ShadowMqConsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.ShadowMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.ShadowMqConsumerEntity;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.MqConfigTemplateDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
@Service
@Slf4j
public class ShadowConsumerServiceImpl implements ShadowConsumerService {

    @Resource
    private ShadowMqConsumerMapper shadowMqConsumerMapper;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;

    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Autowired
    private ShadowMqConsumerDAO shadowMqConsumerDAO;

    @Autowired
    private MqConfigTemplateDAO mqConfigTemplateDAO;


    @Override
    public List<ShadowConsumerOutput> getShadowConsumersByApplicationId(long applicationId) {
        LambdaQueryWrapper<ShadowMqConsumerEntity> query = new LambdaQueryWrapper<>();
        query.eq(ShadowMqConsumerEntity::getApplicationId, applicationId);
        query.eq(ShadowMqConsumerEntity::getStatus, ShadowConsumerConstants.ENABLE);
        List<ShadowMqConsumerEntity> shadowMqConsumerEntities = shadowMqConsumerMapper.selectList(query);
        if (CollectionUtils.isEmpty(shadowMqConsumerEntities)) {
            return Lists.newArrayList();
        }
        return shadowMqConsumerEntities.stream().map(entry -> {
            ShadowConsumerOutput response = new ShadowConsumerOutput();
            response.setId(entry.getId());
            response.setUnionId(null);
            response.setType(ShadowMqConsumerType.of(entry.getType()));
            response.setTopicGroup(entry.getTopicGroup());
            response.setGmtCreate(entry.getCreateTime());
            response.setGmtUpdate(entry.getUpdateTime());
            response.setEnabled(entry.getStatus() == ShadowConsumerConstants.ENABLE);
            response.setDeleted(entry.getDeleted());
            response.setFeature(entry.getFeature());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public ShadowConsumerOutput getMqConsumerById(Long id) {
        ShadowMqConsumerEntity entity = shadowMqConsumerMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        ShadowConsumerOutput response = new ShadowConsumerOutput();
        response.setId(entity.getId());
        response.setUnionId(null);
        response.setType(ShadowMqConsumerType.of(entity.getType()));
        response.setTopicGroup(entity.getTopicGroup());
        response.setEnabled(entity.getStatus() == ShadowConsumerConstants.ENABLE);
        response.setGmtCreate(entity.getCreateTime());
        response.setGmtUpdate(entity.getUpdateTime());
        response.setShadowconsumerEnable(String.valueOf(entity.getStatus()));
        return response;
    }

    @Override
    public PagingList<ShadowConsumerOutput> pageMqConsumers(ShadowConsumerQueryInput request) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR,
                String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
        }
        LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getTopicGroup())) {
            lambdaQueryWrapper.like(ShadowMqConsumerEntity::getTopicGroup, request.getTopicGroup());
        }
        if (request.getType() != null) {
            lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getType, request.getType().name());
        }
        if (request.getEnabled() != null) {
            lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getStatus,
                request.getEnabled() ? ShadowConsumerConstants.ENABLE : ShadowConsumerConstants.DISABLE);
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.getQueryAllowUserIdList())) {
            lambdaQueryWrapper.in(ShadowMqConsumerEntity::getUserId, WebPluginUtils.getQueryAllowUserIdList());
        }
        List<ShadowConsumerOutput> totalResult;
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getApplicationId, request.getApplicationId());
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
        List<ShadowMqConsumerEntity> dbResult = shadowMqConsumerMapper.selectList(lambdaQueryWrapper);
        List<ShadowMqConsumerOutput> amdbResult = Lists.newArrayList();
        if (request.getEnabled() == null) {
            amdbResult = queryAmdbDefaultEntrances(request, application.getApplicationName());
        }
        totalResult = mergeResult(amdbResult, dbResult);
        totalResult = filterResult(request, totalResult);
        return splitPage(request, totalResult);
    }

    private List<ShadowConsumerOutput> filterResult(ShadowConsumerQueryInput request,
        List<ShadowConsumerOutput> totalResult) {
        if (request.getEnabled() != null) {
            if (request.getEnabled()) {
                totalResult = totalResult.stream().filter(ShadowConsumerOutput::getEnabled).collect(
                    Collectors.toList());
            } else {
                totalResult = totalResult.stream().filter(e -> !e.getEnabled()).collect(Collectors.toList());
            }
        }
        if (StringUtils.isNotBlank(request.getTopicGroup())) {
            totalResult = totalResult.stream().filter(e -> e.getTopicGroup().contains(request.getTopicGroup())).collect(
                Collectors.toList());
        }
        if (request.getType() != null) {
            totalResult = totalResult.stream().filter(e -> e.getType() == request.getType()).collect(
                Collectors.toList());
        }
        return totalResult;
    }

    private List<ShadowConsumerOutput> mergeResult(List<ShadowMqConsumerOutput> amdbResult,
        List<ShadowMqConsumerEntity> dbResult) {
        Map<String, ShadowConsumerOutput> amdbMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(amdbResult)) {
            amdbMap = amdbResult.stream()
                .filter(item -> ShadowMqConsumerType.getByName(item.getType()) != null)
                .map(e -> {
                    ShadowConsumerOutput response = new ShadowConsumerOutput();
                    response.setUnionId(
                        MD5Util.getMD5(e.getApplicationName() + "#" + e.getTopicGroup() + "#" + e.getType()));
                    response.setType(ShadowMqConsumerType.of(e.getType()));
                    response.setTopicGroup(e.getTopicGroup());
                    response.setEnabled(e.getStatus() == ShadowConsumerConstants.ENABLE);
                    response.setGmtCreate(e.getCreateTime());
                    response.setGmtUpdate(e.getUpdateTime());
                    response.setCanRemove(false);
                    response.setCanEnableDisable(false);
                    response.setIsManual(false);
                    response.setShadowconsumerEnable(String.valueOf(e.getStatus()));
                    return response;
                })
                .collect(Collectors.toMap(ShadowConsumerOutput::getUnionId, e -> e, (oV, nV) -> nV));
        }
        Map<String, ShadowConsumerOutput> dbMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dbResult)) {
            dbMap = dbResult.stream()
                .filter(item -> ShadowMqConsumerType.getByName(item.getType()) != null)
                .map(e -> {
                    ShadowConsumerOutput response = new ShadowConsumerOutput();
                    response.setId(e.getId());
                    response.setUnionId(
                        MD5Util.getMD5(e.getApplicationName() + "#" + e.getTopicGroup() + "#" + e.getType()));
                    response.setType(ShadowMqConsumerType.of(e.getType()));
                    response.setTopicGroup(e.getTopicGroup());
                    response.setEnabled(e.getStatus() == ShadowConsumerConstants.ENABLE);
                    response.setGmtCreate(e.getCreateTime());
                    response.setGmtUpdate(e.getUpdateTime());
                    response.setUserId(e.getUserId());
                    response.setIsManual(e.getManualTag() == 1);
                    response.setCanRemove(response.getIsManual());
                    response.setShadowconsumerEnable(String.valueOf(e.getStatus()));
                    WebPluginUtils.fillQueryResponse(response);
                    return response;
                })
                .collect(Collectors.toMap(ShadowConsumerOutput::getUnionId, e -> e, (oV, nV) -> nV));
        }
        // 在amdb自动梳理的基础上，补充数据库里面的记录，有的话用数据的记录
        for (Entry<String, ShadowConsumerOutput> dbEntry : dbMap.entrySet()) {
            amdbMap.merge(dbEntry.getKey(), dbEntry.getValue(), (amdbValue, dbValue) -> {
                dbValue.setCanEdit(true);
                dbValue.setCanRemove(dbValue.getIsManual());
                dbValue.setCanEnableDisable(dbValue.getCanEnableDisable());
                return dbValue;
            });
        }
        return Lists.newArrayList(amdbMap.values());
    }

    private List<ShadowMqConsumerOutput> queryAmdbDefaultEntrances(ShadowConsumerQueryInput request,
        String applicationName) {
        List<ServiceInfoDTO> mqTopicGroups = applicationEntranceClient.getMqTopicGroups(applicationName);
        if (CollectionUtils.isEmpty(mqTopicGroups)) {
            return Lists.newArrayList();
        }
        return mqTopicGroups.stream().map(mqTopicGroup -> {
            ShadowMqConsumerOutput shadowMqConsumerOutput = new ShadowMqConsumerOutput();
            shadowMqConsumerOutput.setTopicGroup(
                mqTopicGroup.getServiceName() + "#" + mqTopicGroup.getMethodName());
            shadowMqConsumerOutput.setType(
                MiddlewareTypeGroupEnum.getMiddlewareGroupType(mqTopicGroup.getMiddlewareName()).getType());
            shadowMqConsumerOutput.setApplicationId(request.getApplicationId());
            shadowMqConsumerOutput.setApplicationName(applicationName);
            shadowMqConsumerOutput.setStatus(ShadowConsumerConstants.DISABLE);
            shadowMqConsumerOutput.setDeleted(ShadowConsumerConstants.LIVED);
            // 补充数据
            WebPluginUtils.fillUserData(shadowMqConsumerOutput);
            return shadowMqConsumerOutput;
        }).collect(Collectors.toList());
    }

    private PagingList<ShadowConsumerOutput> splitPage(
        ShadowConsumerQueryInput request,
        List<ShadowConsumerOutput> responses) {
        responses.sort((o1, o2) -> {
            if (o1.getGmtCreate() != null && o2.getGmtCreate() != null) {
                int firstSort = -o1.getGmtCreate().compareTo(o2.getGmtCreate());
                if (firstSort == 0) {
                    return -o1.getId().compareTo(o2.getId());
                }
                return firstSort;
            }
            return 1;
        });
        int total = responses.size();
        int start = Math.min(request.getOffset(), total);
        int end = Math.min((request.getOffset() + request.getPageSize()), total);
        responses = responses.subList(start, end);
        return PagingList.of(responses, total);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createMqConsumers(ShadowConsumerCreateInput request) {
        if (!request.getTopicGroup().contains("#")) {
            throw new RuntimeException("请求参数不正确，Group和Topic以#号拼接");
        }
        String[] split = request.getTopicGroup().split("#");
        if (split.length != 2) {
            throw new RuntimeException("请求参数不正确，Group和Topic中间包含超过1个#");
        }
        ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
        if (application == null) {
            throw new RuntimeException(String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
        }
        List<ShadowMqConsumerEntity> exists = getExists(request.getTopicGroup(), request.getApplicationId(),
            request.getType());
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new RuntimeException(
                String.format("类型为[%s]，对应的[%s]已存在", request.getType().name(), request.getTopicGroup()));
        }
        OperationLogContextHolder.operationType(OpTypes.CREATE);
        OperationLogContextHolder.addVars(Vars.CONSUMER_TYPE, request.getType().name());
        OperationLogContextHolder.addVars(Vars.CONSUMER_TOPIC_GROUP, request.getTopicGroup());
        ShadowMqConsumerEntity shadowMqConsumerEntity = new ShadowMqConsumerEntity();
        shadowMqConsumerEntity.setTopicGroup(request.getTopicGroup());
        shadowMqConsumerEntity.setType(request.getType().name());
        shadowMqConsumerEntity.setApplicationId(application.getApplicationId());
        shadowMqConsumerEntity.setApplicationName(application.getApplicationName());

        Integer status = request.getStatus() == null ? ShadowConsumerConstants.DISABLE : request.getStatus();
        shadowMqConsumerEntity.setStatus(status);
        shadowMqConsumerEntity.setDeleted(ShadowConsumerConstants.LIVED);
        shadowMqConsumerMapper.insert(shadowMqConsumerEntity);
        agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateMqConsumers(ShadowConsumerUpdateInput request) {
        if (!request.getTopicGroup().contains("#")) {
            throw new RuntimeException("请求参数不正确，Group和Topic以#号拼接");
        }
        String[] split = request.getTopicGroup().split("#");
        if (split.length != 2) {
            throw new RuntimeException("请求参数不正确，Group和Topic中间包含超过1个#");
        }
        ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
        if (application == null) {
            throw new RuntimeException(String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
        }
        List<ShadowMqConsumerEntity> exists = getExists(request.getTopicGroup(), request.getApplicationId(),
            request.getType());
        // 同名的自己不算
        exists = exists.stream().filter(item -> !item.getId().equals(request.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new RuntimeException(
                String.format("类型为[%s]，对应的[%s]已存在", request.getType().name(), request.getTopicGroup()));
        }
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars(Vars.CONSUMER_TYPE, request.getType().name());
        OperationLogContextHolder.addVars(Vars.CONSUMER_TOPIC_GROUP, request.getTopicGroup());
        ShadowMqConsumerEntity updateEntity = new ShadowMqConsumerEntity();
        updateEntity.setId(request.getId());
        updateEntity.setTopicGroup(request.getTopicGroup());
        updateEntity.setType(request.getType().name());
        updateEntity.setStatus(request.getStatus());
        shadowMqConsumerMapper.updateById(updateEntity);
        agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteMqConsumers(List<Long> ids) {
        LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ShadowMqConsumerEntity::getId, ids);
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
        List<ShadowMqConsumerEntity> shadowMqConsumerEntities = shadowMqConsumerMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(shadowMqConsumerEntities)) {
            return;
        }
        Long applicationId = shadowMqConsumerEntities.get(0).getApplicationId();
        ApplicationDetailResult application = applicationDAO.getApplicationById(applicationId);
        if (application == null) {
            throw new RuntimeException(String.format("应用id:%s对应的应用不存在", applicationId));
        }
        shadowMqConsumerEntities.forEach(mq -> {
            OperationLogContextHolder.operationType(OpTypes.DELETE);
            OperationLogContextHolder.addVars(Vars.CONSUMER_TYPE, mq.getType());
            OperationLogContextHolder.addVars(Vars.CONSUMER_TOPIC_GROUP, mq.getTopicGroup());
            shadowMqConsumerDAO.removeById(mq.getId());
        });

        agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
    }

    @Override
    public void operateMqConsumers(ShadowConsumersOperateInput requests) {
        // 启用
        ApplicationDetailResult application = applicationDAO.getApplicationById(requests.getApplicationId());
        if (application == null) {
            throw new RuntimeException(String.format("应用id:%s对应的应用不存在", requests.getApplicationId()));
        }
        if (requests.getEnable()) {
            OperationLogContextHolder.operationType(OpTypes.ENABLE);
            requests.getRequests().forEach(request -> {
                if (request.getId() != null) {
                    // 已存在ID的修改数据库状态
                    ShadowMqConsumerEntity entity = new ShadowMqConsumerEntity();
                    entity.setId(request.getId());
                    entity.setStatus(ShadowConsumerConstants.ENABLE);
                    shadowMqConsumerMapper.updateById(entity);
                } else {
                    // 不存在ID的新增一条记录
                    if (request.getTopicGroup() == null || request.getType() == null) {
                        throw new RuntimeException("启用失败，缺少对应的参数");
                    }
                    ShadowMqConsumerEntity shadowMqConsumerEntity = new ShadowMqConsumerEntity();
                    shadowMqConsumerEntity.setTopicGroup(request.getTopicGroup());
                    shadowMqConsumerEntity.setType(request.getType().name());
                    shadowMqConsumerEntity.setApplicationId(application.getApplicationId());
                    shadowMqConsumerEntity.setApplicationName(application.getApplicationName());
                    shadowMqConsumerEntity.setStatus(ShadowConsumerConstants.ENABLE);
                    shadowMqConsumerEntity.setDeleted(ShadowConsumerConstants.LIVED);
                    try {
                        shadowMqConsumerMapper.insert(shadowMqConsumerEntity);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            });
        } else {
            OperationLogContextHolder.operationType(OpTypes.DISABLE);
            List<Long> ids = requests.getRequests().stream().map(ShadowConsumerOperateInput::getId).collect(
                Collectors.toList());
            LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(ShadowMqConsumerEntity::getId, ids);
            lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
            List<ShadowMqConsumerEntity> shadowMqConsumerEntities = shadowMqConsumerMapper.selectList(
                lambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(shadowMqConsumerEntities)) {
                for (ShadowMqConsumerEntity shadowMqConsumerEntity : shadowMqConsumerEntities) {
                    ShadowMqConsumerEntity updateEntity = new ShadowMqConsumerEntity();
                    updateEntity.setId(shadowMqConsumerEntity.getId());
                    updateEntity.setStatus(ShadowConsumerConstants.DISABLE);
                    shadowMqConsumerMapper.updateById(updateEntity);
                }
            }
        }
        agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
    }

    @Override
    public List<ShadowConsumerVO> agentSelect(String appName) {
        LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getStatus, ShadowConsumerConstants.ENABLE);
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getApplicationName, appName);
        List<ShadowMqConsumerEntity> entities = shadowMqConsumerMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        Map<String, List<ShadowMqConsumerEntity>> collect = entities.stream()
            .filter(t -> {
                if (StringUtils.isNotBlank(t.getTopicGroup())) {
                    String[] topicGroup = t.getTopicGroup().trim().split("#");
                    return topicGroup.length == 2;
                } else {
                    return false;
                }
            }).collect(Collectors.groupingBy(ShadowMqConsumerEntity::getType));
        if (MapUtils.isEmpty(collect)) {
            return Lists.newArrayList();
        }
        return collect.entrySet().stream().map(entity -> {
            ShadowConsumerVO shadowConsumerVO = new ShadowConsumerVO();
            shadowConsumerVO.setType(entity.getKey());
            Map<String, Set<String>> topicGroupMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(entity.getValue())) {
                for (ShadowMqConsumerEntity shadowMqConsumerEntity : entity.getValue()) {
                    if (StringUtils.isNotBlank(shadowMqConsumerEntity.getTopicGroup())) {
                        String[] topicGroup = shadowMqConsumerEntity.getTopicGroup().trim().split("#");
                        if (topicGroup.length == 2) {
                            // #后面没有消费组的，属于垃圾数据，过滤掉
                            Set<String> groups = topicGroupMap.get(topicGroup[0]);
                            if (groups == null) {
                                topicGroupMap.put(topicGroup[0], Sets.newHashSet(topicGroup[1]));
                            } else {
                                groups.add(topicGroup[1]);
                            }
                        }
                    }
                }
            }
            shadowConsumerVO.setTopicGroups(topicGroupMap);
            return shadowConsumerVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int allocationUser(ShadowConsumerUpdateUserInput request) {
        if (Objects.isNull(request.getApplicationId())) {
            return 0;
        }
        LambdaQueryWrapper<ShadowMqConsumerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShadowMqConsumerEntity::getApplicationId, request.getApplicationId());
        List<ShadowMqConsumerEntity> shadowMqConsumerEntityList = shadowMqConsumerMapper.selectList(
            queryWrapper);
        if (CollectionUtils.isNotEmpty(shadowMqConsumerEntityList)) {
            for (ShadowMqConsumerEntity entity : shadowMqConsumerEntityList) {
                entity.setUserId(request.getUserId());
                shadowMqConsumerMapper.updateById(entity);
            }
        }
        return 0;
    }

    private List<ShadowMqConsumerEntity> getExists(String topicGroup, Long applicationId, ShadowMqConsumerType type) {
        LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getTopicGroup, topicGroup);
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getType, type.name());
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getApplicationId, applicationId);
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
        return shadowMqConsumerMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public List<SelectVO> queryMqSupportType() {
        List<MqConfigTemplateDetailResult> results = mqConfigTemplateDAO.queryList();
        if (results.isEmpty()) {
            return Collections.emptyList();
        }
        List<SelectVO> vos = Lists.newArrayList();
        results.forEach(mqTemplate -> {
            vos.add(new SelectVO(MQTypeEnum.getCodeByValue(mqTemplate.getEngName()), mqTemplate.getEngName()));
        });

        return vos;
    }

    @Override
    public List<SelectVO> queryMqSupportProgramme(String engName) {
        MqConfigTemplateDetailResult result = mqConfigTemplateDAO.queryOne(engName);
        List<SelectVO> vos = Lists.newArrayList();
        vos.add(new SelectVO("不消费影子topic", String.valueOf(ShadowConsumerConstants.DISABLE)));
        if (result.getShadowconsumerEnable() == 1) {
            vos.add(new SelectVO("消费影子topic", String.valueOf(ShadowConsumerConstants.ENABLE)));
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateMqConsumersV2(ShadowConsumerUpdateInput request) {
        if (Objects.isNull(request.getId())) {
            this.createMqConsumersV2(request,false);
        } else {
            if (!request.getTopicGroup().contains("#")) {
                throw new RuntimeException("请求参数不正确，Group和Topic以#号拼接");
            }
            String[] split = request.getTopicGroup().split("#");
            if (split.length != 2) {
                throw new RuntimeException("请求参数不正确，Group和Topic中间包含超过1个#");
            }
            ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
            if (application == null) {
                throw new RuntimeException(String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
            }
            List<ShadowMqConsumerEntity> exists = getExists(request.getTopicGroup(), request.getApplicationId(),
                    request.getType());
            // 同名的自己不算
            exists = exists.stream().filter(item -> !item.getId().equals(request.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(exists)) {
                throw new RuntimeException(
                        String.format("类型为[%s]，对应的[%s]已存在", request.getType().name(), request.getTopicGroup()));
            }
            OperationLogContextHolder.operationType(OpTypes.UPDATE);
            OperationLogContextHolder.addVars(Vars.CONSUMER_TYPE, request.getType().name());
            OperationLogContextHolder.addVars(Vars.CONSUMER_TOPIC_GROUP, request.getTopicGroup());
            ShadowMqConsumerEntity updateEntity = new ShadowMqConsumerEntity();
            updateEntity.setId(request.getId());
            updateEntity.setTopicGroup(request.getTopicGroup());
            updateEntity.setType(request.getType().name());
            updateEntity.setStatus(Integer.valueOf(request.getShadowconsumerEnable()));
            shadowMqConsumerMapper.updateById(updateEntity);
            agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createMqConsumersV2(ShadowConsumerCreateInput request,Boolean ManualTag) {
        if (!request.getTopicGroup().contains("#")) {
            throw new RuntimeException("请求参数不正确，Group和Topic以#号拼接");
        }
        String[] split = request.getTopicGroup().split("#");
        if (split.length != 2) {
            throw new RuntimeException("请求参数不正确，Group和Topic中间包含超过1个#");
        }
        ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
        if (application == null) {
            throw new RuntimeException(String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
        }
        List<ShadowMqConsumerEntity> exists = getExists(request.getTopicGroup(), request.getApplicationId(),
                request.getType());
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new RuntimeException(
                    String.format("类型为[%s]，对应的[%s]已存在", request.getType().name(), request.getTopicGroup()));
        }
        OperationLogContextHolder.operationType(OpTypes.CREATE);
        OperationLogContextHolder.addVars(Vars.CONSUMER_TYPE, request.getType().name());
        OperationLogContextHolder.addVars(Vars.CONSUMER_TOPIC_GROUP, request.getTopicGroup());
        ShadowMqConsumerEntity shadowMqConsumerEntity = new ShadowMqConsumerEntity();
        shadowMqConsumerEntity.setTopicGroup(request.getTopicGroup());
        shadowMqConsumerEntity.setType(request.getType().name());
        shadowMqConsumerEntity.setApplicationId(application.getApplicationId());
        shadowMqConsumerEntity.setApplicationName(application.getApplicationName());

        Integer status = StringUtils.isBlank(request.getShadowconsumerEnable()) ? ShadowConsumerConstants.DISABLE : Integer.parseInt(request.getShadowconsumerEnable());
        shadowMqConsumerEntity.setStatus(status);
        shadowMqConsumerEntity.setManualTag(ManualTag?1:0);
        shadowMqConsumerEntity.setDeleted(ShadowConsumerConstants.LIVED);
        shadowMqConsumerMapper.insert(shadowMqConsumerEntity);
        agentConfigCacheManager.evictShadowConsumer(application.getApplicationName());
    }

    @Override
    public PagingList<ShadowConsumerOutput> pageMqConsumersV2(ShadowConsumerQueryInputV2 request) {
        ApplicationDetailResult application = applicationDAO.getApplicationById(request.getApplicationId());
        ShadowConsumerQueryInput queryInput = Convert.convert(ShadowConsumerQueryInput.class, request);
        if (application == null) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR,
                    String.format("应用id:%s对应的应用不存在", request.getApplicationId()));
        }
        LambdaQueryWrapper<ShadowMqConsumerEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getTopicGroup())) {
            lambdaQueryWrapper.like(ShadowMqConsumerEntity::getTopicGroup, request.getTopicGroup());
        }
        if (request.getType() != null) {
            lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getType, request.getType());
        }
        if (StringUtils.isNotBlank(request.getShadowconsumerEnable())) {
            lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getStatus, request.getShadowconsumerEnable());
        }
        if (CollectionUtils.isNotEmpty(WebPluginUtils.getQueryAllowUserIdList())) {
            lambdaQueryWrapper.in(ShadowMqConsumerEntity::getUserId, WebPluginUtils.getQueryAllowUserIdList());
        }
        List<ShadowConsumerOutput> totalResult;
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getApplicationId, request.getApplicationId());
        lambdaQueryWrapper.eq(ShadowMqConsumerEntity::getDeleted, ShadowConsumerConstants.LIVED);
        List<ShadowMqConsumerEntity> dbResult = shadowMqConsumerMapper.selectList(lambdaQueryWrapper);
        List<ShadowMqConsumerOutput> amdbResult = Lists.newArrayList();
        if (Objects.nonNull(request.getShadowconsumerEnable())) {
            queryInput.setEnabled(Objects.equals(request.getShadowconsumerEnable(), "1"));
        }
        if (StringUtils.isBlank(request.getShadowconsumerEnable())) {
            amdbResult = queryAmdbDefaultEntrances(queryInput, application.getApplicationName());
        }
        totalResult = mergeResult(amdbResult, dbResult);
        totalResult = filterResult(queryInput, totalResult);
        return splitPage(queryInput, totalResult);
    }


}
