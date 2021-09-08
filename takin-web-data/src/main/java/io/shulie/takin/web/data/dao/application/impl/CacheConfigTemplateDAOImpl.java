package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.CacheConfigTemplateDAO;
import io.shulie.takin.web.data.mapper.mysql.CacheConfigTemplateMapper;
import io.shulie.takin.web.data.model.mysql.CacheConfigTemplateEntity;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 缓存配置模版表(CacheConfigTemplate)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-30 10:28:56
 */
@Service
public class CacheConfigTemplateDAOImpl  extends ServiceImpl<CacheConfigTemplateMapper, CacheConfigTemplateEntity> implements CacheConfigTemplateDAO, MPUtil<CacheConfigTemplateEntity> {

    @Override
    public CacheConfigTemplateDetailResult queryOne(String middlewareType, String engName) {
        LambdaQueryWrapper<CacheConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(CacheConfigTemplateEntity::getEngName,engName)
                .eq(CacheConfigTemplateEntity::getType,middlewareType)
                .eq(CacheConfigTemplateEntity::getStatus,0)
                .eq(CacheConfigTemplateEntity::getIsDeleted,0);
        CacheConfigTemplateEntity entity = getOne(lambdaQueryWrapper);
        return this.convertResult(entity);
    }


    private List<CacheConfigTemplateDetailResult> convertResults( List<CacheConfigTemplateEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
       return entities.stream().map(this::convertResult).collect(Collectors.toList());
    }

    private CacheConfigTemplateDetailResult convertResult(CacheConfigTemplateEntity entity){
        if(Objects.isNull(entity)){
            return null;
        }
        return Convert.convert(CacheConfigTemplateDetailResult.class,entity);
    }
}

