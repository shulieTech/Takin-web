package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.web.data.param.application.AppDatabaseInputParam;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:17 AM
 */
public interface AppDataSourceService {
    /**
     * 保存数据源信息
     *
     * @param param
     */
    void save(AppDatabaseInputParam param);
}
