package io.shulie.takin.web.data.dao.application.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.dao.application.RpcConfigTemplateDAO;
import io.shulie.takin.web.data.mapper.mysql.RpcConfigTemplateMapper;
import io.shulie.takin.web.data.model.mysql.HttpClientConfigTemplateEntity;
import io.shulie.takin.web.data.model.mysql.RpcConfigTemplateEntity;
import io.shulie.takin.web.data.result.application.HttpClientConfigTemplateDetailResult;
import io.shulie.takin.web.data.result.application.RpcConfigTemplateDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * rpc框架配置模版(RpcConfigTemplate)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-25 15:00:26
 */
@Service
public class RpcConfigTemplateDAOImpl extends ServiceImpl<RpcConfigTemplateMapper, RpcConfigTemplateEntity> implements RpcConfigTemplateDAO, MPUtil<RpcConfigTemplateEntity> {

    @Override
    public RpcConfigTemplateDetailResult selectTemplate(String typeName) {
        LambdaQueryWrapper<RpcConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(typeName), RpcConfigTemplateEntity::getEngName, typeName)
                .eq(RpcConfigTemplateEntity::getStatus, 0)
                .eq(RpcConfigTemplateEntity::getIsDeleted, 0);

        RpcConfigTemplateEntity entity = this.getOne(lambdaQueryWrapper);
        return this.getRpcConfigTemplateDetailResult(entity);
    }

    @Override
    public List<RpcConfigTemplateDetailResult> selectList() {
        LambdaQueryWrapper<RpcConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(RpcConfigTemplateEntity::getStatus, 0)
                .eq(RpcConfigTemplateEntity::getIsDeleted, 0);
        List<RpcConfigTemplateEntity> list = list(lambdaQueryWrapper);
        return this.getRpcConfigTemplateDetailResults(list);
    }

    private RpcConfigTemplateDetailResult getRpcConfigTemplateDetailResult(RpcConfigTemplateEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        RpcConfigTemplateDetailResult result = new RpcConfigTemplateDetailResult();
        BeanUtils.copyProperties(entity, result);
        return result;
    }


    private List<RpcConfigTemplateDetailResult> getRpcConfigTemplateDetailResults(List<RpcConfigTemplateEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        return entities.stream().map(this::getRpcConfigTemplateDetailResult).collect(Collectors.toList());
    }
}

