package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;

public interface InterfaceTypeMainDAO {

    InterfaceTypeMainEntity selectByOrder(Integer order);

    /**
     * 根据接口类型名称查询对应的接口类型
     *
     * @param typeName 接口类型名称
     * @return 接口类型
     */
    InterfaceTypeMainEntity selectByName(String typeName);

    InterfaceTypeMainEntity selectById(Long id);

    /**
     * 查询所有的接口类型
     *
     * @return 接口类型
     */
    List<InterfaceTypeMainEntity> selectList();

    Map<Integer, InterfaceTypeMainEntity> selectToMapWithOrderKey();

    /**
     * 唯一性校验
     *
     * @param entity {@link InterfaceTypeMainEntity}
     */
    void checkUnique(InterfaceTypeMainEntity entity);

    void addEntity(InterfaceTypeMainEntity entity);

    Set<String> selectAllAvailableNames();
}
