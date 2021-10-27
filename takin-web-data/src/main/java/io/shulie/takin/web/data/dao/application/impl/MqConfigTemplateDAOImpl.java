package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.MqConfigTemplateDAO;
import io.shulie.takin.web.data.mapper.mysql.MqConfigTemplateMapper;
import io.shulie.takin.web.data.model.mysql.CacheConfigTemplateEntity;
import io.shulie.takin.web.data.model.mysql.MqConfigTemplateEntity;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import io.shulie.takin.web.data.result.application.MqConfigTemplateDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MQ配置模版表(MqConfigTemplate)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-31 15:34:04
 */
@Service
public class MqConfigTemplateDAOImpl  extends ServiceImpl<MqConfigTemplateMapper, MqConfigTemplateEntity>  implements MqConfigTemplateDAO, MPUtil<MqConfigTemplateEntity> {

    /**
     * 获取支持的mq类型
     *
     * @return
     */
    @Override
    public List<MqConfigTemplateDetailResult> queryList() {
        LambdaQueryWrapper<MqConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(MqConfigTemplateEntity::getStatus,0)
                .eq(MqConfigTemplateEntity::getIsDeleted,0);
        List<MqConfigTemplateEntity> entities = list(lambdaQueryWrapper);
        return this.convertResults(entities);
    }

    @Override
    public MqConfigTemplateDetailResult queryOne(String engName) {
        LambdaQueryWrapper<MqConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(MqConfigTemplateEntity::getEngName,engName)
                .eq(MqConfigTemplateEntity::getStatus,0)
                .eq(MqConfigTemplateEntity::getIsDeleted,0);
        MqConfigTemplateEntity entity = getOne(lambdaQueryWrapper);
        return this.convertResult(entity);
    }

    private List<MqConfigTemplateDetailResult> convertResults(List<MqConfigTemplateEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertResult).collect(Collectors.toList());
    }

    private MqConfigTemplateDetailResult convertResult(MqConfigTemplateEntity entity){
        if(Objects.isNull(entity)){
            return null;
        }
        return Convert.convert(MqConfigTemplateDetailResult.class,entity);
    }
}

