package io.shulie.takin.web.biz.service;

import java.time.LocalDateTime;
import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;

/**
 * 业务活动
 *
 * @author shiyajian
 * create: 2020-12-30
 */
public interface ActivityService {

    /**
     * 新增业务活动
     *
     * @param request
     */
    void createActivity(ActivityCreateRequest request);

    /**
     * 新增虚拟业务活动
     *
     * @param request
     */
    void createVirtualActivity(VirtualActivityCreateRequest request);

    /**
     * 更新业务活动
     *
     * @param request
     */
    void updateActivity(ActivityUpdateRequest request);

    /**
     * 更新虚拟业务活动
     *
     * @param request
     */
    void updateVirtualActivity(VirtualActivityUpdateRequest request);

    /**
     * 删除业务活动
     *
     * @param activityId
     */
    void deleteActivity(Long activityId);

    /**
     * 分页业务活动
     *
     * @param request
     * @return
     */
    PagingList<ActivityListResponse> pageActivities(ActivityQueryRequest request);

    /**
     * 获取业务活动详情
     *
     * @param activityId 业务活动id
     * @return 详情
     */
    ActivityResponse getActivityById(Long activityId);

    ActivityResponse getActivityWithMetricsById(ActivityInfoQueryRequest request);

    ActivityResponse getActivityWithMetricsByIdForReport(Long activityId, LocalDateTime start, LocalDateTime end);

    ActivityResponse getActivityByIdWithoutTopology(Long id);

    ActivityVerifyResponse verifyActivity(ActivityVerifyRequest request);

    ActivityVerifyResponse getVerifyStatus(Long activityId);

    void setActivityNodeState(long activityId, String serviceName, String ownerApps, boolean state);

    List<ActivityNodeState> getActivityNodeServiceState(long activityId);
}
