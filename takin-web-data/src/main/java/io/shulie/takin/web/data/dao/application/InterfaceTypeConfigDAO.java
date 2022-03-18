package io.shulie.takin.web.data.dao.application;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.InterfaceTypeConfigEntity;

public interface InterfaceTypeConfigDAO {

    /**
     * 根据接口类型查询对应支持的配置类型
     *
     * @param typeId 接口类型
     * @return 配置类型
     */
    List<InterfaceTypeConfigEntity> selectByTypeId(Long typeId);

    void addEntity(InterfaceTypeConfigEntity entity);

    /**
     * 唯一性校验
     *
     * @param entity {@link InterfaceTypeConfigEntity}
     */
    void checkUnique(InterfaceTypeConfigEntity entity);

    List<InterfaceTypeConfigEntity> selectList();

    void deleteById(Long id);
}
