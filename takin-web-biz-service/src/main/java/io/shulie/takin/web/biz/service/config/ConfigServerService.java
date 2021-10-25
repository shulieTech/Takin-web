package io.shulie.takin.web.biz.service.config;

import io.shulie.takin.web.biz.pojo.request.config.UpdateConfigServerRequest;

/**
 * 服务配置服务层
 *
 * @author liuchuan
 * @date 2021/10/20 2:03 下午
 */
public interface ConfigServerService {

    /**
     * 根据配置的 key, 更新配置值
     *
     * 租户隔离的
     * 没有的, 就创建, 有的就修改
     * redis 缓存清空
     *
     * @param updateRequest 需要的相关参数
     */
    void update(UpdateConfigServerRequest updateRequest);

}
