package io.shulie.takin.cloud.biz.service.scene;

import io.shulie.takin.adapter.api.model.response.scenemanage.SynchronizeRequest;

/**
 * 场景同步服务 - 新
 *
 * @author 张天赐
 */
public interface SceneSynchronizeService {
    /**
     * 同步
     *
     * @param request 入参信息
     *                <ul>
     *                <li>脚本主键</li>
     *                <li>脚本解析结果</li>
     *                </ul>
     * @return 同步事务标识
     */
    String synchronize(SynchronizeRequest request);
}
