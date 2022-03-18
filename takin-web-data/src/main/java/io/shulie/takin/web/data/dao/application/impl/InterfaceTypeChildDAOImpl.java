package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.application.InterfaceTypeChildDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfaceTypeChildMapper;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class InterfaceTypeChildDAOImpl extends ServiceImpl<InterfaceTypeChildMapper, InterfaceTypeChildEntity>
    implements InterfaceTypeChildDAO, MPUtil<InterfaceTypeChildEntity> {

    @Override
    public InterfaceTypeChildEntity selectByName(String typeName) {
        return selectByName(typeName, true);
    }

    @Override
    public List<InterfaceTypeChildEntity> selectList() {
        LambdaQueryWrapper<InterfaceTypeChildEntity> wrapper = this.getLambdaQueryWrapper().eq(
            InterfaceTypeChildEntity::getIsDeleted, 0);
        return list(wrapper);
    }

    @Override
    public Map<String, InterfaceTypeChildEntity> selectToMapWithNameKey() {
        return selectList().stream().collect(
            Collectors.toMap(InterfaceTypeChildEntity::getEngName, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public void checkUnique(InterfaceTypeChildEntity entity) {
        String engName = entity.getEngName();
        if (selectByName(engName, false) != null) {
            throw new TakinWebException(
                ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, String.format("[engName = %s] 配置已存在", engName));
        }
    }

    @Override
    public void addEntity(InterfaceTypeChildEntity entity) {
        this.save(entity);
    }

    @Override
    public Set<String> selectAllAvailableNames() {
        LambdaQueryWrapper<InterfaceTypeChildEntity> wrapper = this.getLambdaQueryWrapper()
            .select(InterfaceTypeChildEntity::getEngName)
            .eq(InterfaceTypeChildEntity::getIsDeleted, 0);
        return list(wrapper).stream().map(InterfaceTypeChildEntity::getEngName).collect(Collectors.toSet());
    }

    public InterfaceTypeChildEntity selectByName(String typeName, boolean unDelete) {
        if (StringUtils.isBlank(typeName)) {
            return null;
        }
        LambdaQueryWrapper<InterfaceTypeChildEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeChildEntity::getEngName, typeName)
            .eq(unDelete, InterfaceTypeChildEntity::getIsDeleted, 0);
        return getOne(wrapper);
    }
}
