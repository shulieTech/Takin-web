package io.shulie.takin.cloud.data.dao.scene.task.impl;

import java.util.Date;

import javax.annotation.Resource;

import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskVarietyDAO;
import io.shulie.takin.cloud.data.mapper.mysql.PressureTaskVarietyMapper;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskVarietyEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class PressureTaskVarietyDAOImpl implements PressureTaskVarietyDAO {

    @Value("${pressure.variety.record: true}")
    private boolean pressureVarietyRecord;
    @Resource
    private PressureTaskVarietyMapper mapper;

    @Override
    public void save(PressureTaskVarietyEntity entity) {
        if (pressureVarietyRecord) {
            entity.setGmtCreate(new Date());
            mapper.insert(entity);
        }
    }
}
