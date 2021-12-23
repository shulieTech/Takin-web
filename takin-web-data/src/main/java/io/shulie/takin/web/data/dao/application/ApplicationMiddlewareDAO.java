package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.web.data.param.application.CreateApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.PageApplicationMiddlewareParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationMiddlewareParam;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareStatusAboutCountResult;

/**
 * 应用中间件(ApplicationMiddleware)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:37
 */
public interface ApplicationMiddlewareDAO {

    /**
     * 应用中间件列表 分页
     *
     * @param pageApplicationMiddlewareParam 请求参数
     * @return 分页列表
     */
    IPage<ApplicationMiddlewareListResult> page(PageApplicationMiddlewareParam pageApplicationMiddlewareParam);

    /**
     * 应用中间件根据应用id统计
     * status 不传, 全部
     *
     * @param applicationId 应用id
     * @param status 状态 @see io.shulie.takin.web.data.model.mysql.ApplicationMiddlewareEntity
     * @return 相关统计
     */
    Long countByApplicationIdAndStatus(Long applicationId, Integer status);

    /**
     * 批量更新, 根据 id
     *
     * @param updateParamList 更新列表
     * @return 是否成功
     */
    boolean updateBatchById(List<UpdateApplicationMiddlewareParam> updateParamList);

    /**
     * 根据应用id删除中间件
     *
     * @param applicationId 应用id
     * @return 是否成功
     */
    boolean removeByApplicationId(Long applicationId);

    /**
     * 批量插入应用中间件
     *
     * @param createApplicationMiddlewareParamList 创建中间件列表
     * @return 是否成功
     */
    boolean insertBatch(List<CreateApplicationMiddlewareParam> createApplicationMiddlewareParamList);

    /**
     * 根据 applicationId 查询中间件列表
     *
     * @return 中间件列表
     * @param applicationId 应用id
     */
    List<ApplicationMiddlewareListResult> listByApplicationId(Long applicationId);

    /**
     * 根据状态, 查询状态对应的统计
     * 不传筛选的状态列表, 查询全部
     *
     * @param applicationId 应用id
     * @param statusList 状态列表
     * @return 状态对应统计
     */
    List<ApplicationMiddlewareStatusAboutCountResult> listCountByApplicationIdAndStatusAndGroupByStatus(Long applicationId, List<Integer> statusList);

    /**
     * 应用名称列表, 状态列表 查询 状态统计,
     * 根据 应用名称, 状态值分组
     *
     * @param applicationIds 应用名称列表
     * @param statusList 状态列表
     * @return 状态统计列表
     */
    List<ApplicationMiddlewareStatusAboutCountResult> listStatusCountByAndGroupByApplicationNameListAndStatus(
        List<Long> applicationIds, List<Integer> statusList);

}

