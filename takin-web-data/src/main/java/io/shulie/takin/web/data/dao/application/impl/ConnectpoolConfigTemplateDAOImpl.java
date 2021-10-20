package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.attach.plugin.dynamic.Type;
import io.shulie.takin.web.data.dao.application.ConnectpoolConfigTemplateDAO;
import io.shulie.takin.web.data.mapper.mysql.ConnectpoolConfigTemplateMapper;
import io.shulie.takin.web.data.model.mysql.CacheConfigTemplateEntity;
import io.shulie.takin.web.data.model.mysql.ConnectpoolConfigTemplateEntity;
import io.shulie.takin.web.data.result.application.CacheConfigTemplateDetailResult;
import io.shulie.takin.web.data.result.application.ConnectpoolConfigTemplateDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 连接池配置模版表(ConnectpoolConfigTemplate)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-08-30 10:31:04
 */
@Service
public class ConnectpoolConfigTemplateDAOImpl  extends ServiceImpl<ConnectpoolConfigTemplateMapper, ConnectpoolConfigTemplateEntity>  implements ConnectpoolConfigTemplateDAO, MPUtil<ConnectpoolConfigTemplateEntity> {

    @Override
    public ConnectpoolConfigTemplateDetailResult queryOne(String middlewareType, String engName) {

        LambdaQueryWrapper<ConnectpoolConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ConnectpoolConfigTemplateEntity::getEngName,engName)
                .eq(ConnectpoolConfigTemplateEntity::getType,middlewareType)
                .eq(ConnectpoolConfigTemplateEntity::getStatus,0)
                .eq(ConnectpoolConfigTemplateEntity::getIsDeleted,0);
        ConnectpoolConfigTemplateEntity entity = getOne(lambdaQueryWrapper);
        return this.convertResult(entity);
    }

    @Override
    public ConnectpoolConfigTemplateDetailResult queryOne(String engName) {
        return this.queryOne(Type.MiddleWareType.LINK_POOL.value(),engName);
    }

    @Override
    public List<ConnectpoolConfigTemplateDetailResult> queryList() {
        LambdaQueryWrapper<ConnectpoolConfigTemplateEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(ConnectpoolConfigTemplateEntity::getStatus,0)
                .eq(ConnectpoolConfigTemplateEntity::getIsDeleted,0);
        return this.convertResults(this.list(lambdaQueryWrapper));
    }

    private List<ConnectpoolConfigTemplateDetailResult> convertResults(List<ConnectpoolConfigTemplateEntity> entities){
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(this::convertResult).collect(Collectors.toList());
    }

    private ConnectpoolConfigTemplateDetailResult convertResult(ConnectpoolConfigTemplateEntity entity){
        if(Objects.isNull(entity)){
            return null;
        }
        return Convert.convert(ConnectpoolConfigTemplateDetailResult.class,entity);
    }
}

