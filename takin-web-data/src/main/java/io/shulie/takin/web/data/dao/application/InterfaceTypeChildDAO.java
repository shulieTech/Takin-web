package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;

public interface InterfaceTypeChildDAO {

    /**
     * 根据接口类型名称查询对应的接口子类型
     *
     * @param typeName 接口类型名称
     * @return 接口类型
     */
    InterfaceTypeChildEntity selectByName(String typeName);

    /**
     * 查询所有的接口子类型
     *
     * @return 接口类型
     */
    List<InterfaceTypeChildEntity> selectList();

    Map<String, InterfaceTypeChildEntity> selectToMapWithNameKey();

    /**
     * 唯一性校验
     *
     * @param entity {@link InterfaceTypeChildEntity}
     */
    void checkUnique(InterfaceTypeChildEntity entity);

    void addEntity(InterfaceTypeChildEntity entity);

    Set<String> selectAllAvailableNames();
}
