package io.shulie.takin.web.data.dao.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.secure.SecureUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDsDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsManageDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateMqComsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import io.shulie.takin.web.data.param.application.ApplicationDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceMqConsumerQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
@Service
public class PressureResourceRelateMqComsumerDAOImpl implements PressureResourceRelateMqComsumerDAO {
    @Resource
    private PressureResourceRelateMqConsumerMapper pressureResourceRelateMqConsumerMapper;

    @Resource
    private ApplicationDsDAO applicationDsDAO;

    @Resource
    private ApplicationDsManageDAO applicationDsManageDAO;

    @Override
    public PagingList<PressureResourceRelateMqConsumerEntity> pageList(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = this.getWrapper(param);
        Page<PressureResourceRelateMqConsumerEntity> page = new Page<>(param.getCurrent() + 1, param.getPageSize());
        queryWrapper.orderByDesc("gmt_modified");
        IPage<PressureResourceRelateMqConsumerEntity> pageList = pressureResourceRelateMqConsumerMapper.selectPage(page, queryWrapper);

        List<Long> dsManageIds = pageList.getRecords().stream().filter(entity -> entity.getRelateDsManageId() != null).map(entity -> entity.getRelateDsManageId()).collect(Collectors.toList());
        if (!dsManageIds.isEmpty()) {
            List<ApplicationDsManageEntity> manageEntities = applicationDsManageDAO.listByIds(dsManageIds);
            Map<Long, ApplicationDsManageEntity> mappings = new HashMap<>();
            manageEntities.forEach(applicationDsManageEntity -> mappings.put(applicationDsManageEntity.getId(), applicationDsManageEntity));

            // 把application_ds_manage的属性填充到配置上
            pageList.getRecords().stream().forEach(entity -> {
                if (entity.getRelateDsManageId() != null) {
                    populateProperties(entity, mappings.get(entity.getRelateDsManageId()));
                }
            });
        }

        if (pageList.getRecords().isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(pageList.getRecords(), pageList.getTotal());
    }

    /**
     * 新增
     *
     * @param mqConsumerEntity
     */
    @Override
    public void add(PressureResourceRelateMqConsumerEntity mqConsumerEntity) {
        pressureResourceRelateMqConsumerMapper.insert(mqConsumerEntity);
    }

    @Override
    public List<PressureResourceRelateMqConsumerEntity> queryList(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = this.getWrapper(param);
        List<PressureResourceRelateMqConsumerEntity> resultLists = pressureResourceRelateMqConsumerMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(resultLists)) {
            return Collections.emptyList();
        }
        return resultLists;
    }

    /**
     * 保存或更新
     *
     * @param mqConsumerEntityList
     */
    @Override
    public void saveOrUpdate(List<PressureResourceRelateMqConsumerEntity> mqConsumerEntityList) {
        if (CollectionUtils.isEmpty(mqConsumerEntityList)) {
            return;
        }
        // 每次梳理链路时，如果没有关联上旧表的mq，都是尝试关联下
        mqConsumerEntityList.stream().forEach(entity -> {

            Map<String, Object> params = new HashMap<>();
            params.put("tenant_id", entity.getTenantId());
            params.put("env_code", entity.getEnvCode());
            params.put("resource_id", entity.getResourceId());
            params.put("`topic`", entity.getTopic());
            params.put("`group`", entity.getGroup());
            params.put("mq_type", entity.getMqType());
            // 根据唯一键查询
            List<PressureResourceRelateMqConsumerEntity> entities = pressureResourceRelateMqConsumerMapper.selectByMap(params);
            if (entities.isEmpty()) {
                entity.setRelateDsManageId(getRelateDsManageId(entity));
                pressureResourceRelateMqConsumerMapper.insert(entity);
                return;
            }
            // 查到了数据
            PressureResourceRelateMqConsumerEntity exists = entities.get(0);
            // 关联上application_ds_manage表
            if ("KAFKA-其他".equals(exists.getMqType())) {
                if (exists.getRelateDsManageId() != null) {
                    return;
                }
                Long relateDsManageId = getRelateDsManageId(exists);
                if (relateDsManageId != null) {
                    exists.setRelateDsManageId(relateDsManageId);
                    exists.setGmtModified(new Date());
                    pressureResourceRelateMqConsumerMapper.updateById(exists);
                }
                return;
            }
            return;
        });
    }

    private Long getRelateDsManageId(PressureResourceRelateMqConsumerEntity entity) {
        ApplicationDsQueryParam param = new ApplicationDsQueryParam();
        param.setApplicationId(entity.getApplicationId());
        param.setUrl(entity.getTopic());
        List<ApplicationDsResult> dsResults = applicationDsDAO.queryList(param);
        if (!dsResults.isEmpty()) {
            return dsResults.get(0).getId();
        }
        return null;
    }

    private QueryWrapper<PressureResourceRelateMqConsumerEntity> getWrapper(PressureResourceMqConsumerQueryParam param) {
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = new QueryWrapper<>();
        if (param == null) {
            return queryWrapper;
        }
        // 模糊查询
        if (StringUtils.isNotBlank(param.getTopic())) {
            queryWrapper.eq("`topic`", param.getTopic());
        }
        if (StringUtils.isNotBlank(param.getGroup())) {
            queryWrapper.eq("`group`", param.getGroup());
        }
        if (param.getResourceId() != null) {
            queryWrapper.eq("resource_id", param.getResourceId());
        }
        if (StringUtils.isNotBlank(param.getMqType())) {
            queryWrapper.eq("mq_type", param.getMqType());
        }
        if (param.getConsumerTag() != null) {
            queryWrapper.eq("consumer_tag", param.getConsumerTag());
        }
        if (StringUtils.isNotBlank(param.getQueryTopicGroup())) {
            queryWrapper.and(tmp -> tmp.like("`topic`", param.getQueryTopicGroup()).or().like("`group`", param.getQueryTopicGroup()));
        }
        if (StringUtils.isNotBlank(param.getQueryApplicationName())) {
            queryWrapper.like("application_name", param.getQueryApplicationName());
        }
        return queryWrapper;
    }

    private void populateProperties(PressureResourceRelateMqConsumerEntity consumer, ApplicationDsManageEntity entity) {
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
        feature.put("consumerThreadCount", object.getInteger("messageConsumeThreadCount"));
        consumer.setFeature(JSON.toJSONString(feature));
    }
}
