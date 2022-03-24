package io.shulie.takin.web.biz.service;

import java.util.List;

public interface WaterlineService {
    List<String> getAllActivityNames();

    List<String> getAllApplicationsByActivity(String activityName);

    List<String> getAllNodesByApplicationName(String applicationName);
}
