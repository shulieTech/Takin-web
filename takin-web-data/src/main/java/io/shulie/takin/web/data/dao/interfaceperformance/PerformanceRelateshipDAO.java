package io.shulie.takin.web.data.dao.interfaceperformance;

import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/6/7 6:53 下午
 */
public interface PerformanceRelateshipDAO {
    InterfacePerformanceConfigSceneRelateShipEntity relationShipEntityById(Long apiId);
}
