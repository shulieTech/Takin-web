package io.shulie.takin.cloud.biz.service.scenetask;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;

/**
 * 动态TPS服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface DynamicTpsService {
    /**
     * 获取动态TPS目标值
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @param md5      线程组md5
     * @return 值-可能为空
     */
    Double get(SceneTaskQueryTpsInput input);

    /**
     * 通过报告获取静态TPS目标
     *
     * @param reportId 报告主键
     * @param md5      脚本节点md5
     * @return TPS目标
     */
    double getStatic(long reportId, String md5);

    /**
     * 设置动态TPS目标值
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @param tenantId 租户主键
     * @param md5      线程组md5
     * @param value    目标值
     */

    void set(SceneTaskUpdateTpsInput input);
}
