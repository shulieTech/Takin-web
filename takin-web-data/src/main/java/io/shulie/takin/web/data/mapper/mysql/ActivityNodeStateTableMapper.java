package io.shulie.takin.web.data.mapper.mysql;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;

import java.util.Map;
import java.util.List;

public interface ActivityNodeStateTableMapper extends BaseMapper<ActivityNodeState> {
    void setActivityNodeState(ActivityNodeState activityNodeState);

    List<ActivityNodeState> getActivityNodes(long activityId);

    void removeActivityNodeByActivityIdAndOwnerApp(ActivityNodeState activityNodeState);

    List<Map<String, String>> findActivityIdByServiceName(@Param(value = "customerId") Long customerId, @Param(value = "appName") String appName, @Param(value = "entrance") String entrance);
}
