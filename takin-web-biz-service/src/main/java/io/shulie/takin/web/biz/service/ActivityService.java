package io.shulie.takin.web.biz.service;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.activity.*;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;

import java.time.LocalDateTime;
import java.util.List;

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
    Long createActivity(ActivityCreateRequest request);

    void createActivityWithoutAMDB(ActivityCreateRequest request);

    /**
     * 新增虚拟业务活动
     *
     * @param request
     */
    Long createVirtualActivity(VirtualActivityCreateRequest request);

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
     * @return 详情
     */
    ActivityResponse getActivityById(ActivityInfoQueryRequest activityInfoQueryRequest);
    ActivityResponse getActivityWithMetricsById(ActivityInfoQueryRequest request);
    ActivityResponse getActivityWithMetricsByIdForReport(Long activityId, LocalDateTime start, LocalDateTime end);
    ActivityBottleneckResponse getBottleneckByActivityList(ApplicationVisualInfoResponse applicationVisualInfoResponse, LocalDateTime startTime, LocalDateTime endTime);

    ActivityResponse getActivityByIdWithoutTopology(Long id);

    ActivityVerifyResponse verifyActivity(ActivityVerifyRequest request);

    ActivityVerifyResponse getVerifyStatus(Long activityId);

    void setActivityNodeState(long activityId, String serviceName, String ownerApps, boolean state);

    List<ActivityNodeState> getActivityNodeServiceState(long activityId);

    BusinessLinkManageTableEntity getActivityByName(String activityName);

    BusinessLinkManageTableEntity getActivity(ActivityCreateRequest request);

    /**
     * 根据条件查询业务活动
     * @param request
     * @return
     */
    List<ActivityListResponse> queryNormalActivities(ActivityResultQueryRequest request);
}
