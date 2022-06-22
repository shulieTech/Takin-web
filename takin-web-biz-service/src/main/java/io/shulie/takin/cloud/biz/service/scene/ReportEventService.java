package io.shulie.takin.cloud.biz.service.scene;

import java.util.Map;

/**
 * @author qianshui
 * @date 2020/7/20 下午3:40
 */
public interface ReportEventService {
    /**
     * 查询和计算RT分布
     *
     * @param tableName 表名
     * @param bindRef   绑定关联
     * @return 计算结果
     */
    Map<String, String> queryAndCalcRtDistribute(String tableName, String bindRef);

}
