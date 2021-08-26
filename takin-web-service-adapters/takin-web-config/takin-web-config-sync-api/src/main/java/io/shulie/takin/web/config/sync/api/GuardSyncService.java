package io.shulie.takin.web.config.sync.api;

import java.util.List;

import io.shulie.takin.web.config.entity.Guard;

/**
 * 挡板
 * 可以理解为 Mock 方法，当方法执行到这里，会返回配置的值，不会实现里面具体的逻辑；
 *
 * @author shiyajian
 * create: 2020-09-01
 */
public interface GuardSyncService {

    /**
     * 同步挡板信息
     */
    void syncGuard(String namespace, String applicationName, List<Guard> newGuards);

}
