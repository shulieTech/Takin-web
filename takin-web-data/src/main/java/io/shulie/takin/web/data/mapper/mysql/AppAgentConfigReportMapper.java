package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AppAgentConfigReportEntity;

/**
 * agent配置上报详情(AppAgentConfigReport)表数据库 mapper
 *
 * @author 南风
 * @date 2021-09-28 17:27:21
 */
public interface AppAgentConfigReportMapper extends BaseMapper<AppAgentConfigReportEntity> {

    /**
     * 删除指定时间之前的数据
     *
     * @param date yyyy-MM-dd HH:mm:ss
     */
    @InterceptorIgnore(tenantLine = "true")
    void selfDelete(String date);
}


