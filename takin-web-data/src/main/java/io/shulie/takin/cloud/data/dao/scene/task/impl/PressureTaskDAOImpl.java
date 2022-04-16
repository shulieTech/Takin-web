package io.shulie.takin.cloud.data.dao.scene.task.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskVarietyDAO;
import io.shulie.takin.cloud.data.mapper.mysql.PressureTaskMapper;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskVarietyEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class PressureTaskDAOImpl implements PressureTaskDAO {

    @Resource
    private PressureTaskMapper pressureTaskMapper;
    @Resource
    private PressureTaskVarietyDAO pressureTaskVarietyDAO;

    @Override
    public PressureTaskEntity selectById(Long id) {
        return pressureTaskMapper.selectById(id);
    }

    @Override
    public void deleteById(Long taskId) {
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setId(taskId);
        entity.setGmtUpdate(new Date());
        entity.setIsDeleted(1);
        pressureTaskMapper.updateById(entity);
    }

    @Override
    public void save(PressureTaskEntity entity) {
        pressureTaskMapper.insert(entity);
    }

    @Override
    public void updateResourceAssociation(String resourceId, Long jobId) {
        pressureTaskMapper.update(new PressureTaskEntity() {{setJobId(jobId);}},
            Wrappers.lambdaQuery(PressureTaskEntity.class).eq(PressureTaskEntity::getResourceId, resourceId));
    }

    @Override
    public void updateById(PressureTaskEntity entity) {
        entity.setExceptionMsg(StringUtils.substring(entity.getExceptionMsg(),0 , 255));
        pressureTaskMapper.updateById(entity);
    }

    @Override
    public void updateStatus(Long taskId, PressureTaskStateEnum state, String message) {
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setId(taskId);
        entity.setStatus(state.ordinal());
        entity.setExceptionMsg(StringUtils.substring(message,0 , 255));
        entity.setGmtUpdate(new Date());
        pressureTaskMapper.updateById(entity);
        pressureTaskVarietyDAO.save(PressureTaskVarietyEntity.of(taskId, state, message));
    }

    @Override
    public void updatePressureTaskMessageByResourceId(String resourceId, String message) {
        if (StringUtils.isNotBlank(resourceId) && StringUtils.isNotBlank(message)) {
            pressureTaskMapper.update(new PressureTaskEntity() {{setExceptionMsg(StringUtils.substring(message, 0, 255));}},
                Wrappers.lambdaQuery(PressureTaskEntity.class).eq(PressureTaskEntity::getResourceId, resourceId));
        }
    }
}
