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
import io.shulie.takin.web.data.dao.application.RemoteCallConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.RemoteCallConfigMapper;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Repository;

@Repository
public class RemoteCallConfigDAOImpl extends ServiceImpl<RemoteCallConfigMapper, RemoteCallConfigEntity>
    implements RemoteCallConfigDAO, MPUtil<RemoteCallConfigEntity> {

    @Override
    public List<RemoteCallConfigEntity> selectList() {
        LambdaQueryWrapper<RemoteCallConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(RemoteCallConfigEntity::getIsDeleted, 0);
        return list(wrapper);
    }

    @Override
    public Map<Integer, RemoteCallConfigEntity> selectToMapWithOrderKey() {
        return selectList().stream().collect(
            Collectors.toMap(RemoteCallConfigEntity::getValueOrder, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public Map<Long, RemoteCallConfigEntity> selectToMap() {
        return selectList().stream().collect(
            Collectors.toMap(RemoteCallConfigEntity::getId, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public RemoteCallConfigEntity selectByOrder(Integer order) {
        return selectByOrder(order, true);
    }

    @Override
    public RemoteCallConfigEntity selectByName(String engName) {
        return selectByName(engName, true);
    }

    @Override
    public void checkUnique(RemoteCallConfigEntity entity) {
        String engName = entity.getEngName();
        if (selectByName(engName, false) != null) {
            throw new TakinWebException(
                ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR, String.format("[engName = %s] 配置已存在", engName));
        }
        Integer valueOrder = entity.getValueOrder();
        if (selectByOrder(valueOrder, false) != null) {
            RemoteCallConfigEntity configEntity = selectMaxOrder();
            String extra = "";
            if (configEntity != null) {
                extra = ", 目前最大值为：" + configEntity.getValueOrder();
            }
            throw new TakinWebException(ExceptionCode.REMOTE_CALL_CONFIG_CHECK_ERROR,
                String.format("[valueOrder = %s] 配置已存在%s", valueOrder, extra));
        }
    }

    @Override
    public void addEntity(RemoteCallConfigEntity entity) {
        save(entity);
    }

    @Override
    public Set<String> selectAllAvailableNames() {
        LambdaQueryWrapper<RemoteCallConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .select(RemoteCallConfigEntity::getEngName)
            .eq(RemoteCallConfigEntity::getIsDeleted, 0);
        return list(wrapper).stream().map(RemoteCallConfigEntity::getEngName).collect(Collectors.toSet());
    }

    private RemoteCallConfigEntity selectMaxOrder() {
        LambdaQueryWrapper<RemoteCallConfigEntity> wrapper = this.getLimitOneLambdaQueryWrapper()
            .select(RemoteCallConfigEntity::getValueOrder)
            .eq(RemoteCallConfigEntity::getIsDeleted, 0)
            .orderByDesc(RemoteCallConfigEntity::getValueOrder);
        return getOne(wrapper);
    }

    public RemoteCallConfigEntity selectByName(String engName, boolean unDelete) {
        if (engName == null) {
            return null;
        }
        LambdaQueryWrapper<RemoteCallConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(RemoteCallConfigEntity::getEngName, engName)
            .eq(unDelete, RemoteCallConfigEntity::getIsDeleted, 0);
        return getOne(wrapper);
    }

    public RemoteCallConfigEntity selectByOrder(Integer order, boolean unDelete) {
        if (Objects.isNull(order)) {
            return null;
        }
        LambdaQueryWrapper<RemoteCallConfigEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(RemoteCallConfigEntity::getValueOrder, order)
            .eq(unDelete, RemoteCallConfigEntity::getIsDeleted, 0);
        return this.getOne(wrapper);
    }
}
