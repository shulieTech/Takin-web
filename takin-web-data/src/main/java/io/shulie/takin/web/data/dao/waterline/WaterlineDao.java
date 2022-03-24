package io.shulie.takin.web.data.dao.waterline;

import io.shulie.takin.web.data.param.activity.ActivityQueryParam;

import java.util.List;

public interface WaterlineDao {
    List<String> getAllActivityNames(ActivityQueryParam param);

    List<String> getAllApplicationsByActivity(ActivityQueryParam param, String activityName);

    List<String> getAllNodesByApplicationName(ActivityQueryParam param, String applicationName);
}
