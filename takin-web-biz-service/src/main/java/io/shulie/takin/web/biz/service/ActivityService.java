package io.shulie.takin.web.biz.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityResultQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ListApplicationRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.biz.pojo.response.activity.BusinessApplicationListResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;

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

    Long createActivityWithoutAMDB(ActivityCreateRequest request);

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

    ActivityBottleneckResponse getBottleneckByActivityList(ApplicationVisualInfoResponse applicationVisualInfoResponse,
        LocalDateTime startTime, LocalDateTime endTime,
        Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap);

    ActivityResponse getActivityByIdWithoutTopology(Long id);

    ActivityVerifyResponse verifyActivity(ActivityVerifyRequest request);

    ActivityVerifyResponse getVerifyStatus(Long activityId);

    void setActivityNodeState(long activityId, String serviceName, String ownerApps, boolean state);

    List<ActivityNodeState> getActivityNodeServiceState(long activityId);

    BusinessLinkManageTableEntity getActivityByName(String activityName);

    BusinessLinkManageTableEntity getActivity(ActivityCreateRequest request);

    /**
     * 根据业务活动id,查询关联应用名
     *
     * @param activityId
     * @return
     */
    List<String> processAppNameByBusinessActiveId(Long activityId);

    /**
     * 根据条件查询业务活动
     *
     * @param request
     * @return
     */
    List<ActivityListResponse> queryNormalActivities(ActivityResultQueryRequest request);

    /**
     * 根据业务活动ids, 获得应用列表
     *
     * @param businessActivityIds 业务活动ids
     * @param applicationName 应用名称, 搜索用
     * @return 应用列表
     */
    List<BusinessApplicationListResponse> listApplicationByBusinessActivityIds(List<Long> businessActivityIds,
        String applicationName);

    /**
     * 根据业务流程ids, 获得应用列表
     *
     * @param listApplicationRequest 所需要的入参
     * @return 应用列表
     */
    PagingList<BusinessApplicationListResponse> listApplicationByBusinessFlowIds(ListApplicationRequest listApplicationRequest);

    void clearCategory(List<Long> categoryIds);
}
