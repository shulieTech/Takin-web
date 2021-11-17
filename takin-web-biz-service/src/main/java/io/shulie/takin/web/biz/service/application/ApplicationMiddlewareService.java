package io.shulie.takin.web.biz.service.application;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.request.application.ListApplicationMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareCountResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareListResponse;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;

/**
 * 应用中间件(ApplicationMiddleware)service
 *
 * @author liuchuan
 * @date 2021-06-30 16:11:28
 */
public interface ApplicationMiddlewareService {

    /**
     * 应用中间件列表 分页
     *
     * @param listApplicationMiddlewareRequest 请求参数
     * @return 分页列表
     */
    PagingList<ApplicationMiddlewareListResponse> page(ListApplicationMiddlewareRequest listApplicationMiddlewareRequest);

    /**
     * 应用中间件相关统计
     *
     * @param applicationId 应用id
     * @return 应用中间件相关统计
     */
    ApplicationMiddlewareCountResponse countSome(Long applicationId);

    /**
     * 应用下的中间件 重新比对 基础库
     *
     * @param applicationId 应用id
     */
    void compare(Long applicationId);

    /**
     * 比较
     *
     * @param results 应用中间件列表
     */
    List<UpdateApplicationMiddlewareParam> doCompare(List<ApplicationMiddlewareListResult> results);

    /**
     * agent 应用上报中间件
     *
     * @param pushMiddlewareRequest 推送需要的参数
     */
    void pushMiddlewareList(PushMiddlewareRequest pushMiddlewareRequest);

    /**
     * 根据应用名称列表, 获得名称对应的get中状态统计
     *
     * @param applicationIds 应用ids
     * @return 名称对应的get中状态统计
     */
    Map<String, Map<Integer, Integer>> getApplicationNameAboutStatusCountMap(List<Long> applicationIds);

}
