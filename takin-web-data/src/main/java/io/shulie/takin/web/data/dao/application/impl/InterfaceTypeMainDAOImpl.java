package io.shulie.takin.web.data.dao.application.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.application.InterfaceTypeMainDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfaceTypeMainMapper;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class InterfaceTypeMainDAOImpl extends ServiceImpl<InterfaceTypeMainMapper, InterfaceTypeMainEntity>
    implements InterfaceTypeMainDAO, MPUtil<InterfaceTypeMainEntity> {

    @Override
    public InterfaceTypeMainEntity selectByName(String typeName) {
        return selectByName(typeName, true);
    }

    @Override
    public InterfaceTypeMainEntity selectById(Long id) {
        return getById(id);
    }

    @Override
    public InterfaceTypeMainEntity selectByOrder(Integer order) {
        return selectByOrder(order, true);
    }

    @Override
    public List<InterfaceTypeMainEntity> selectList() {
        LambdaQueryWrapper<InterfaceTypeMainEntity> wrapper = this.getLambdaQueryWrapper().eq(
            InterfaceTypeMainEntity::getIsDeleted, 0);
        return list(wrapper);
    }

    @Override
    public Map<Integer, InterfaceTypeMainEntity> selectToMapWithOrderKey() {
        return selectList().stream().collect(
            Collectors.toMap(InterfaceTypeMainEntity::getValueOrder, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public void checkUnique(InterfaceTypeMainEntity entity) {
        String engName = entity.getEngName();
        if (selectByName(engName, false) != null) {
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("[engName = %s] 配置已存在", engName));
        }
        Integer valueOrder = entity.getValueOrder();
        if (selectByOrder(valueOrder, false) != null) {
            InterfaceTypeMainEntity mainEntity = selectMaxOrder();
            String extraMessage = "";
            if (mainEntity != null) {
                extraMessage = ", 目前最大值为：" + mainEntity.getValueOrder();
            }
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("[valueOrder = %s] 配置已存在%s", valueOrder, extraMessage));
        }
    }

    @Override
    public void addEntity(InterfaceTypeMainEntity entity) {
        this.save(entity);
    }

    @Override
    public Set<String> selectAllAvailableNames() {
        LambdaQueryWrapper<InterfaceTypeMainEntity> wrapper = this.getLambdaQueryWrapper()
            .select(InterfaceTypeMainEntity::getEngName)
            .eq(InterfaceTypeMainEntity::getIsDeleted, 0);
        return list(wrapper).stream().map(InterfaceTypeMainEntity::getEngName).collect(Collectors.toSet());
    }

    private InterfaceTypeMainEntity selectMaxOrder() {
        LambdaQueryWrapper<InterfaceTypeMainEntity> wrapper = this.getLimitOneLambdaQueryWrapper()
            .select(InterfaceTypeMainEntity::getValueOrder)
            .eq(InterfaceTypeMainEntity::getIsDeleted, 0)
            .orderByDesc(InterfaceTypeMainEntity::getValueOrder);
        return getOne(wrapper);
    }

    private InterfaceTypeMainEntity selectByName(String typeName, boolean unDelete) {
        if (StringUtils.isBlank(typeName)) {
            return null;
        }
        LambdaQueryWrapper<InterfaceTypeMainEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeMainEntity::getEngName, typeName)
            .eq(unDelete, InterfaceTypeMainEntity::getIsDeleted, 0);
        return getOne(wrapper);
    }

    private InterfaceTypeMainEntity selectByOrder(Integer order, boolean unDelete) {
        if (Objects.isNull(order)) {
            return null;
        }
        LambdaQueryWrapper<InterfaceTypeMainEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(InterfaceTypeMainEntity::getValueOrder, order)
            .eq(unDelete, InterfaceTypeMainEntity::getIsDeleted, 0);
        return this.getOne(wrapper);
    }
}
