package io.shulie.takin.web.data.dao.waterline;

import com.pamirs.takin.entity.domain.entity.collector.Metrics;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;

import java.util.List;

public interface WaterlineDao {
    List<String> getAllActivityNames(ActivityQueryParam param);

    List<String> getAllApplicationsByActivity(ActivityQueryParam param, String activityName);

    List<String> getAllNodesByApplicationName(String applicationName);

    List<String> getApplicationNamesWithIds(List<String> ids);

    List<Metrics> getApplicationNodesNumber(List<String> names);

    List<String> getApplicationTags(String applicationName);
}
