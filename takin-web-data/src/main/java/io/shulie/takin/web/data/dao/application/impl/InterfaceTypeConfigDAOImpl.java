package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.application.InterfaceTypeConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfaceTypeConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeConfigEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Repository;

@Repository
public class InterfaceTypeConfigDAOImpl extends ServiceImpl<InterfaceTypeConfigMapper, InterfaceTypeConfigEntity>
    implements InterfaceTypeConfigDAO, MPUtil<InterfaceTypeConfigEntity> {

    @Override
    public List<InterfaceTypeConfigEntity> selectList() {
        LambdaQueryWrapper<InterfaceTypeConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeConfigEntity::getIsDeleted, 0);
        return list(wrapper);
    }

    @Override
    public List<InterfaceTypeConfigEntity> selectByTypeId(Long typeId) {
        if (Objects.isNull(typeId)) {
            return null;
        }
        LambdaQueryWrapper<InterfaceTypeConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeConfigEntity::getInterfaceTypeId, typeId)
            .eq(InterfaceTypeConfigEntity::getIsDeleted, 0);
        return this.list(wrapper);
    }

    @Override
    public void addEntity(InterfaceTypeConfigEntity entity) {
        save(entity);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public void checkUnique(InterfaceTypeConfigEntity entity) {
        Long typeId = entity.getInterfaceTypeId();
        Long configId = entity.getConfigId();
        LambdaQueryWrapper<InterfaceTypeConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeConfigEntity::getInterfaceTypeId, typeId)
            .eq(InterfaceTypeConfigEntity::getConfigId, configId);
        if (this.getOne(wrapper) != null) {
            throw new TakinWebException(
                ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("[childTypeName = %s, configName = %s] 配置已存在", entity.getChildTypeName(), entity.getConfigName()));
        }
    }
}
