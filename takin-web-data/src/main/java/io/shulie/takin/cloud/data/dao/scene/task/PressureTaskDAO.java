package io.shulie.takin.cloud.data.dao.scene.task;

import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;

public interface PressureTaskDAO {

    PressureTaskEntity selectById(Long id);

    void deleteById(Long taskId);

    void save(PressureTaskEntity entity);

    void updateResourceAssociation(String resourceId, Long jobId);

    void updateById(PressureTaskEntity entity);

    void updateStatus(Long taskId, PressureTaskStateEnum state, String message);

    void updatePressureTaskMessageByResourceId(String resourceId, String message);
}
