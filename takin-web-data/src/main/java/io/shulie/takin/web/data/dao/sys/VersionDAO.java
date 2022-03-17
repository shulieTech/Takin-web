package io.shulie.takin.web.data.dao.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.VersionEntity;

public interface VersionDAO extends IService<VersionEntity> {

    VersionEntity selectLeast();

    /**
     * 查询最新版本
     *
     * @return 最新版本
     */
    VersionEntity selectLeastOnVersionCondition(boolean eq, String curVersion);

    /**
     * 检查版本号是否已存在
     *
     * @param version 版本号
     * @return true-已存在
     */
    boolean exists(String version);

    /**
     * 发布新版本
     *
     * @param entity 版本
     * @return true-发布成功
     */
    boolean publish(VersionEntity entity);
}
