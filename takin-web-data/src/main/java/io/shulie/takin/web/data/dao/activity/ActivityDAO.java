package io.shulie.takin.web.data.dao.activity;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.activity.ActivityCreateParam;
import io.shulie.takin.web.data.param.activity.ActivityExistsQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityUpdateParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
public interface ActivityDAO {

    /**
     * 判断是否存在
     * @param param
     * @return
     */
    List<Long> exists(ActivityExistsQueryParam param);

    /**
     * 创建正常业务活动，因为涉及老版兼容问题，这里独立 新版本业务活动createActivityNew
     * @param createParam
     * @return
     */
    int createActivity(ActivityCreateParam createParam);

    /**
     * 新版创建业务活动
     * @param createParam
     * @return
     */
    int createActivityNew(ActivityCreateParam createParam);

    /**
     * @param activityId
     * @return
     */
    ActivityResult getActivityById(Long activityId);

    /**
     * 创建正常业务活动，因为涉及老版兼容问题，这里独立 新版本业务活动createActivityNew
     * @param updateParam
     * @return
     */
    int updateActivity(ActivityUpdateParam updateParam);

    /**
     * 更新创建业务活动
     * @param updateParam
     * @return
     */
    int updateActivityNew(ActivityUpdateParam updateParam);

    /**
     * 删除业务活动
     * @param activityId
     */
    void deleteActivity(Long activityId);

    PagingList<ActivityListResult> pageActivities(ActivityQueryParam param);

    /**
     * 获取业务活动
     * @param param
     * @return
     */
    List<ActivityListResult> getActivityList(ActivityQueryParam param);
}
