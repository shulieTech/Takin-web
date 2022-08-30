package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.web.data.model.mysql.PressureResourceAppDataSourceEntity;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:22 AM
 */
public interface AppDataSourceDAO {
    void saveOrUpdate(List<PressureResourceAppDataSourceEntity> entitys);
}
