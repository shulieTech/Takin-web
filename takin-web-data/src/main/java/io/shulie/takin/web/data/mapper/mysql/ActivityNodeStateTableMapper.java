package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import org.apache.ibatis.annotations.Param;

public interface ActivityNodeStateTableMapper extends BaseMapper<ActivityNodeState> {
    void setActivityNodeState(ActivityNodeState activityNodeState);

    List<ActivityNodeState> getActivityNodes(long activityId);

    void removeActivityNodeByActivityIdAndOwnerApp(ActivityNodeState activityNodeState);

    List<Map<String,String>> findActivityIdByServiceName(@Param(value = "tenantId") Long tenantId, @Param(value = "appName") String appName, @Param(value = "entrance") String entrance);

    /**
     * 通过应用名称+入口名称查询业务活动
     * @param appName
     * @param entrance
     * @return
     */
    List<Map<String,String>> findActivityByServiceName(@Param(value = "appName") String appName, @Param(value = "entrance") String entrance);
}
