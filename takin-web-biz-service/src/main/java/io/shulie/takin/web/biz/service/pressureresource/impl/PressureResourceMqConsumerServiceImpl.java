package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MqConsumerFeature;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceMqConsumerService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceMqComsumerVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateMqComsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsManageMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationDsUpdateParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceMqConsumerQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
@Service
public class PressureResourceMqConsumerServiceImpl implements PressureResourceMqConsumerService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceMqConsumerServiceImpl.class);

    @Resource
    private PressureResourceRelateMqConsumerMapper pressureResourceRelateMqConsumerMapper;
    @Resource
    private PressureResourceRelateMqComsumerDAO pressureResourceRelateMqComsumerDAO;

    @Resource
    private ApplicationDsDAO applicationDsDAO;

    @Resource
    private ApplicationDAO applicationDAO;

    @Resource
    private ApplicationDsManageMapper applicationDsManageMapper;

    /**
     * 创建影子消费者
     *
     * @param request
     */
    @Override
    public void create(PressureResourceMqConsumerCreateInput request) {
        validata(request);
        PressureResourceMqConsumerQueryParam queryParam = new PressureResourceMqConsumerQueryParam();
        queryParam.setResourceId(request.getResourceId());
        queryParam.setTopic(request.getTopic());
        queryParam.setGroup(request.getGroup());
        queryParam.setMqType(request.getMqType());
        List<PressureResourceRelateMqConsumerEntity> exists = pressureResourceRelateMqComsumerDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new RuntimeException(
                    String.format("类型为[%s]，对应的topic[%s] group[%s]已存在",
                            request.getMqType(),
                            request.getTopic(),
                            request.getGroup()));
        }
        PressureResourceRelateMqConsumerEntity shadowMqConsumerEntity = convertEntity(request);
        shadowMqConsumerEntity.setId(null);
        shadowMqConsumerEntity.setGmtCreate(new Date());
        shadowMqConsumerEntity.setGmtModified(new Date());
        // kafka-其他集群模式
        if ("KAFKA-其他".equals(request.getMqType())) {
            Long dsId = insertShadowClusterKafkaDsManage(shadowMqConsumerEntity);
            shadowMqConsumerEntity.setRelateDsManageId(dsId);
        }
        pressureResourceRelateMqComsumerDAO.add(shadowMqConsumerEntity);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceMqComsumerVO> list(PressureResourceMqConsumerQueryRequest request) {
        PressureResourceMqConsumerQueryParam param = new PressureResourceMqConsumerQueryParam();
        BeanUtils.copyProperties(request, param);
        PagingList<PressureResourceRelateMqConsumerEntity> pageList = pressureResourceRelateMqComsumerDAO.pageList(param);
        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateMqConsumerEntity> source = pageList.getList();
        List<PressureResourceMqComsumerVO> returnList = source.stream().map(configDto -> {
            PressureResourceMqComsumerVO vo = new PressureResourceMqComsumerVO();
            BeanUtils.copyProperties(configDto, vo);
            Long applicationId = configDto.getApplicationId();
            if(applicationId != null){
                vo.setApplicationId(String.valueOf(applicationId));
                vo.setApplicationName(applicationDAO.getApplicationById(applicationId).getApplicationName());
            }
            vo.setId(String.valueOf(configDto.getId()));
            // 转换下feature
            if (StringUtils.isNotBlank(vo.getFeature())) {
                vo.setMqConsumerFeature(JSON.parseObject(vo.getFeature(), MqConsumerFeature.class));
            }
            // kafka的时候,如果是生产者,不需要展示消费组,设置为空
            if (vo.getMqType().contains("KAFKA") && vo.getComsumerType() == 0) {
                vo.setGroup("");
            }
            return vo;
        }).collect(Collectors.toList());

        return PagingList.of(returnList, pageList.getTotal());
    }

    private void validata(PressureResourceMqConsumerCreateInput request) {
    }

    /**
     * 修改
     *
     * @param request
     */
    @Override
    public void update(PressureResourceMqConsumerCreateInput request) {
        if (request.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateMqConsumerEntity entity = pressureResourceRelateMqConsumerMapper.selectById(request.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceMqConsumerQueryParam queryParam = new PressureResourceMqConsumerQueryParam();
        queryParam.setResourceId(request.getResourceId());
        queryParam.setTopic(request.getTopic());
        queryParam.setGroup(request.getGroup());
        queryParam.setMqType(request.getMqType());
        List<PressureResourceRelateMqConsumerEntity> exists = pressureResourceRelateMqComsumerDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(exists)) {
            // 判断是否属于同一个Id
            PressureResourceRelateMqConsumerEntity mqConsumer = exists.get(0);
            if (!mqConsumer.getId().equals(request.getId())) {
                throw new RuntimeException(
                        String.format("类型为[%s]，对应的topic[%s] group[%s]已存在",
                                request.getMqType(),
                                request.getTopic(),
                                request.getGroup()));
            }
        }
        // 更新
        PressureResourceRelateMqConsumerEntity updateEntity = convertEntity(request);
        updateEntity.setGmtModified(new Date());

        // 更新应用配置的旧表
        if ("KAFKA-其他".equals(request.getMqType())) {
            if (entity.getRelateDsManageId() == null) {
                Long dsManageId = insertShadowClusterKafkaDsManage(updateEntity);
                updateEntity.setRelateDsManageId(dsManageId);
            } else {
                ApplicationDsUpdateParam updateParam = new ApplicationDsUpdateParam();
                updateParam.setId(entity.getRelateDsManageId());
                updateParam.setUrl(request.getTopic());
                updateParam.setStatus(entity.getConsumerTag());
                String config = buildConfig(updateEntity);
                updateParam.setConfig(config);
                updateParam.setParseConfig(config);
                applicationDsDAO.update(updateParam);
            }
        }
        pressureResourceRelateMqConsumerMapper.updateById(updateEntity);

    }

    /**
     * 处理消费状态
     *
     * @param input
     */
    @Override
    public void processConsumerTag(PressureResourceMqConsumerCreateInput input) {
        if (CollectionUtils.isEmpty(input.getIds()) && input.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数未指定");
        }
        PressureResourceRelateMqConsumerEntity updateEntity = new PressureResourceRelateMqConsumerEntity();
        updateEntity.setConsumerTag(input.getConsumerTag());
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(input.getIds())) {
            queryWrapper.in("id", input.getIds());
        }
        if (input.getId() != null) {
            queryWrapper.eq("id", input.getId());
        }
        pressureResourceRelateMqConsumerMapper.update(updateEntity, queryWrapper);

        // 更新application_ds_manage表
        List<PressureResourceRelateMqConsumerEntity> consumerEntities = pressureResourceRelateMqConsumerMapper.selectList(queryWrapper);
        List<Long> dsManageIds = consumerEntities.stream().filter(entity -> entity.getRelateDsManageId() != null).map(entity -> entity.getRelateDsManageId()).collect(Collectors.toList());
        if (!dsManageIds.isEmpty()) {
            ApplicationDsManageEntity entity = new ApplicationDsManageEntity();
            entity.setStatus(input.getConsumerTag());
            QueryWrapper<ApplicationDsManageEntity> dsManageEntityQueryWrapper = new QueryWrapper<>();
            if (CollectionUtils.isNotEmpty(input.getIds())) {
                queryWrapper.in("id", dsManageIds);
            }
            applicationDsManageMapper.update(entity, dsManageEntityQueryWrapper);
        }

    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        PressureResourceRelateMqConsumerEntity consumerEntity = pressureResourceRelateMqConsumerMapper.selectById(id);
        if (consumerEntity == null) {
            return;
        }
        if (consumerEntity.getRelateDsManageId() != null) {
            applicationDsManageMapper.deleteById(consumerEntity.getRelateDsManageId());
        }
        pressureResourceRelateMqConsumerMapper.deleteById(id);
    }

    private PressureResourceRelateMqConsumerEntity convertEntity(PressureResourceMqConsumerCreateInput request) {
        PressureResourceRelateMqConsumerEntity shadowMqConsumerEntity = new PressureResourceRelateMqConsumerEntity();
        shadowMqConsumerEntity.setId(request.getId());
        shadowMqConsumerEntity.setResourceId(request.getResourceId());
        shadowMqConsumerEntity.setTopic(request.getTopic());
        shadowMqConsumerEntity.setGroup(StringUtils.isBlank(request.getGroup()) ? "default" : request.getGroup());
        shadowMqConsumerEntity.setBrokerAddr(request.getBrokerAddr());
        shadowMqConsumerEntity.setTopicTokens(request.getTopicTokens());
        shadowMqConsumerEntity.setSystemIdToken(request.getSystemIdToken());
        shadowMqConsumerEntity.setMqType(request.getMqType());
        String applicationId = request.getApplicationId();
        if(StringUtils.isNotBlank(applicationId)){
            shadowMqConsumerEntity.setApplicationId(Long.valueOf(applicationId));
        }
        // 是否消费
        shadowMqConsumerEntity.setConsumerTag(request.getConsumerTag());
        shadowMqConsumerEntity.setComsumerType(request.getComsumerType());
        shadowMqConsumerEntity.setIsCluster(request.getIsCluster());
        // 设置来源标识
        shadowMqConsumerEntity.setType(request.getType());
        if (request.getMqConsumerFeature() != null) {
            shadowMqConsumerEntity.setFeature(JSON.toJSONString(request.getMqConsumerFeature()));
        }
        return shadowMqConsumerEntity;
    }

    /**
     * 写入application_ds_manage表
     *
     * @param entity
     * @return
     */
    private Long insertShadowClusterKafkaDsManage(PressureResourceRelateMqConsumerEntity entity) {
        ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();
        createParam.setUrl(entity.getTopic());
        createParam.setUrl(entity.getTopic());
        String config = buildConfig(entity);
        createParam.setConfig(config);
        createParam.setParseConfig(config);
        createParam.setApplicationId(entity.getApplicationId());
        if (entity.getApplicationId() != null && entity.getApplicationName() == null) {
            entity.setApplicationName(applicationDAO.getApplicationById(entity.getApplicationId()).getApplicationName());
        }
        createParam.setApplicationName(entity.getApplicationName());
        createParam.setCreateTime(new Date());
        createParam.setUpdateTime(new Date());
        createParam.setDbType(4);
        createParam.setDsType(5);
        WebPluginUtils.fillUserData(createParam);
        createParam.setStatus(entity.getConsumerTag());

        // 是否有存储过的数据
        ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        queryParam.setApplicationId(entity.getApplicationId());
        queryParam.setUrl(entity.getTopic());
        List<ApplicationDsResult> dsResults = applicationDsDAO.queryList(new ApplicationDsQueryParam());
        if (!dsResults.isEmpty()) {
            Optional<ApplicationDsResult> optional = dsResults.stream().filter(applicationDsResult -> config.equals(JSON.parseObject(applicationDsResult.getConfig()).toJSONString())).findFirst();
            if (optional.isPresent()) {
                return optional.get().getId();
            }
        }
        return Long.valueOf(applicationDsDAO.insert(createParam));
    }

    private String buildConfig(PressureResourceRelateMqConsumerEntity entity) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("key", entity.getTopic());
        objectMap.put("topic", entity.getTopic());
        objectMap.put("topicTokens", entity.getTopicTokens());
        objectMap.put("group", entity.getGroup());
        objectMap.put("brokerAddr", entity.getBrokerAddr());
        objectMap.put("systemIdToken", entity.getSystemIdToken());
        if (entity.getFeature() != null) {
            Map<String, Object> feature = JSON.parseObject(entity.getFeature(), Map.class);
            objectMap.put("clusterName", feature.get("clusterName"));
            objectMap.put("monitorUrl", feature.get("clusterAddr"));
            objectMap.put("poolSize", feature.get("providerThreadCount"));
            objectMap.put("messageConsumeThreadCount", feature.get("consumerThreadCount"));
        }
        return JSON.toJSONString(objectMap);
    }
}
