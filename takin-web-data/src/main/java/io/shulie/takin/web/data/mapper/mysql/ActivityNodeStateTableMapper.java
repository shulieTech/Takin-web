package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ActivityNodeStateTableMapper extends BaseMapper<ActivityNodeState> {
    void setActivityNodeState(ActivityNodeState activityNodeState);

    List<ActivityNodeState> getActivityNodes(long activityId);

    void removeActivityNodeByActivityIdAndOwnerApp(ActivityNodeState activityNodeState);

    Map<String,String> findActivityIdByServiceName(@Param(value = "appName") String appName, @Param(value = "entrance") String entrance);
}
