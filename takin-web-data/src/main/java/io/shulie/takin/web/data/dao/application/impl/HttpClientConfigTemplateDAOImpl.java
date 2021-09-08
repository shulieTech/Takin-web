package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.HttpClientConfigTemplateDAO;
import io.shulie.takin.web.data.mapper.mysql.HttpClientConfigTemplateMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.HttpClientConfigTemplateEntity;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.HttpClientConfigTemplateDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * http-client配置模版表(HttpClientConfigTemplate)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-25 14:56:55
 */
@Service
public class HttpClientConfigTemplateDAOImpl extends ServiceImpl<HttpClientConfigTemplateMapper, HttpClientConfigTemplateEntity> implements HttpClientConfigTemplateDAO, MPUtil<HttpClientConfigTemplateEntity> {

    @Override
    public HttpClientConfigTemplateDetailResult selectTemplate(String typeName) {
        LambdaQueryWrapper<HttpClientConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(typeName), HttpClientConfigTemplateEntity::getEngName, typeName)
                .eq(HttpClientConfigTemplateEntity::getStatus, 1)
                .eq(HttpClientConfigTemplateEntity::getIsDeleted, 0);

        HttpClientConfigTemplateEntity entity = this.getOne(lambdaQueryWrapper);
        return this.getHttpClientConfigTemplateDetailResult(entity);
    }

    @Override
    public List<HttpClientConfigTemplateDetailResult> selectList() {
        LambdaQueryWrapper<HttpClientConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(HttpClientConfigTemplateEntity::getStatus, 1)
                .eq(HttpClientConfigTemplateEntity::getIsDeleted, 0);
        List<HttpClientConfigTemplateEntity> list = this.list(lambdaQueryWrapper);
        return this.getHttpClientConfigTemplateDetailResults(list);
    }

    private List<HttpClientConfigTemplateDetailResult> getHttpClientConfigTemplateDetailResults(List<HttpClientConfigTemplateEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(entity -> this.getHttpClientConfigTemplateDetailResult(entity)).collect(Collectors.toList());
    }

    private HttpClientConfigTemplateDetailResult getHttpClientConfigTemplateDetailResult(HttpClientConfigTemplateEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        HttpClientConfigTemplateDetailResult result = new HttpClientConfigTemplateDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}

