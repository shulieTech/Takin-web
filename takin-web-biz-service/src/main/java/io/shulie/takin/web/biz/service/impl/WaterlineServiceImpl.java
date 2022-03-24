package io.shulie.takin.web.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.web.biz.service.WaterlineService;
import io.shulie.takin.web.data.dao.waterline.WaterlineDao;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WaterlineServiceImpl implements WaterlineService {

    @Autowired
    private WaterlineDao waterlineDao;

    @Override
    public List<String> getAllActivityNames() {
        ActivityQueryParam param = new ActivityQueryParam();
        WebPluginUtils.fillQueryParam(param);
        return waterlineDao.getAllActivityNames(param);
    }

    @Override
    public List<String> getAllApplicationsByActivity(String activityName) {
        ActivityQueryParam param = new ActivityQueryParam();
        WebPluginUtils.fillQueryParam(param);
        return waterlineDao.getAllApplicationsByActivity(param,activityName);
    }

    @Override
    public List<String> getAllNodesByApplicationName(String applicationName) {
        ActivityQueryParam param = new ActivityQueryParam();
        WebPluginUtils.fillQueryParam(param);
        return waterlineDao.getAllNodesByApplicationName(param,applicationName);
    }
}
