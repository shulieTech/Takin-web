package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.AppRemoteCallTemplateMappingDAO;
import io.shulie.takin.web.data.mapper.mysql.AppRemoteCallTemplateMappingMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallTemplateMappingEntity;
import io.shulie.takin.web.data.result.application.AppRemoteCallTemplateMappingDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 远程调用接口类型与模板映射(AppRemoteCallTemplateMapping)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-09-09 14:44:42
 */
@Service
public class AppRemoteCallTemplateMappingDAOImpl extends ServiceImpl<AppRemoteCallTemplateMappingMapper, AppRemoteCallTemplateMappingEntity>
        implements AppRemoteCallTemplateMappingDAO, MPUtil<AppRemoteCallTemplateMappingEntity> {

    @Override
    public AppRemoteCallTemplateMappingDetailResult getOneByInterfacetype(String interfacetype) {
        LambdaQueryWrapper<AppRemoteCallTemplateMappingEntity> lambdaQueryWrapper = this.getLambdaQueryWrapper()
                .eq(AppRemoteCallTemplateMappingEntity::getInterfacetype,interfacetype)
                .eq(AppRemoteCallTemplateMappingEntity::getStatus,0)
                .eq(AppRemoteCallTemplateMappingEntity::getIsDeleted,0);
        AppRemoteCallTemplateMappingEntity entity = getOne(lambdaQueryWrapper);
        return this.convertEntity(entity);
    }


    private AppRemoteCallTemplateMappingDetailResult convertEntity(AppRemoteCallTemplateMappingEntity entity){
        if(Objects.isNull(entity)){
            return null;
        }
        return Convert.convert(AppRemoteCallTemplateMappingDetailResult.class,entity);
    }
}

