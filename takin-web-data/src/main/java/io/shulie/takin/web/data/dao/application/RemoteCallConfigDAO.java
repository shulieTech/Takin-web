package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;

public interface RemoteCallConfigDAO {

    List<RemoteCallConfigEntity> selectList();

    Map<Integer, RemoteCallConfigEntity> selectToMapWithOrderKey();

    Map<Long, RemoteCallConfigEntity> selectToMap();

    RemoteCallConfigEntity selectByOrder(Integer order);

    /**
     * 唯一性校验
     *
     * @param entity {@link RemoteCallConfigEntity}
     */
    void checkUnique(RemoteCallConfigEntity entity);

    void addEntity(RemoteCallConfigEntity entity);

    RemoteCallConfigEntity selectByName(String engName);

    Set<String> selectAllAvailableNames();
}
