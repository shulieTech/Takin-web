package io.shulie.takin.web.data.dao.waterline.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pamirs.takin.entity.domain.entity.collector.Metrics;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.waterline.WaterlineDao;
import io.shulie.takin.web.data.mapper.mysql.ApplicationNodeMapper;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class WaterlineDaoImpl implements WaterlineDao {

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    @Resource
    private ApplicationNodeMapper applicationNodeMapper;

    @Autowired
    private AmdbClientProperties properties;

    private static final String GET_APPLICATION_NODES = "/amdb/db/api/appInstance/selectByBatchAppParams";

    @Override
    public List<String> getAllActivityNames(ActivityQueryParam param) {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::isPersistence, 1);
        List<BusinessLinkManageTableEntity> entities = businessLinkManageTableMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.stream().map(BusinessLinkManageTableEntity::getLinkName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<String> getAllApplicationsByActivity(ActivityQueryParam param, String activityName) {
        LambdaQueryWrapper<BusinessLinkManageTableEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getIsDeleted, 0);
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::isPersistence, 1);
        lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getLinkName, activityName);
        List<BusinessLinkManageTableEntity> entities = businessLinkManageTableMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.stream().map(BusinessLinkManageTableEntity::getApplicationName).distinct().collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<String> getAllNodesByApplicationName(String applicationName) {
//        applicationNodeMapper.getAllNodesByApplicationName(applicationName);
        return doGetAllNodesByApplicationName(applicationName);
    }

    @Override
    public List<String> getApplicationNamesWithIds(List<String> ids) {
        return applicationNodeMapper.getApplicationNamesWithIds(ids);
    }

    @Override
    public List<Metrics> getApplicationNodesNumber(List<String> names) {
        return null;
    }

    @Override
    public List<String> getApplicationTags(String applicationName) {
        return applicationNodeMapper.getApplicationTags(applicationName);
    }

    private List<String> doGetAllNodesByApplicationName(String applicationName) {
        String url = properties.getUrl().getAmdb() + GET_APPLICATION_NODES;
        HashMap request = new HashMap();
        request.put("appNames", applicationName);
        try {
            AmdbResult<List<Object>> appDataResult = AmdbHelper.builder().httpMethod(
                            HttpMethod.POST)
                    .url(url)
                    .param(request)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("根据应用名称查询大数据性能数据")
                    .list(Object.class);
            List data = appDataResult.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                return (List<String>) data.stream().map(obj -> {
                    JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
                    return (json.get("ipAddress").toString());
                }).collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }
}
