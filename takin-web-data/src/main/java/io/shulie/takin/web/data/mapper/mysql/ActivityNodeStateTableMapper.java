package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;

import java.util.List;

public interface ActivityNodeStateTableMapper extends BaseMapper<ActivityNodeState> {
    void setActivityNodeState(ActivityNodeState activityNodeState);

    List<ActivityNodeState> getActivityNodes(long activityId);

    void removeActivityNodeByActivityIdAndOwnerApp(ActivityNodeState activityNodeState);
}
