package io.shulie.takin.web.biz.service.pressureresource;

import java.util.List;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceCommonService {
    /**
     * 自动处理压测准备
     */
    void processAutoPressureResource();

    /**
     * 自动处理压测准备-应用自动梳理
     */
    void processAutoPressureResourceRelate(Long resourceId);

    /**
     * 推送变更到Redis
     *
     * @param resoureIds
     */
    void pushRedis(Long... resoureIds);

    /**
     * 从Redis里面获取数据
     */
    List<Long> getResourceIdsFormRedis();

    /**
     * 从Redis里面获取数据
     */
    void delResourceIdToRedis(Long id);

    /**
     * 修改以后同步数据源相关到应用里面
     *
     * @param resouceId
     */
    void syncDs(Long resouceId);
}
