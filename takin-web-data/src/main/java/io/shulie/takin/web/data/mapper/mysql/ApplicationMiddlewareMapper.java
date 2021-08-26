package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationMiddlewareEntity;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareListResult;
import io.shulie.takin.web.data.result.application.ApplicationMiddlewareStatusAboutCountResult;
import org.apache.ibatis.annotations.Param;

/**
 * 应用中间件(ApplicationMiddleware)表数据库 mapper
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:36
 */
public interface ApplicationMiddlewareMapper extends BaseMapper<ApplicationMiddlewareEntity> {

    /**
     * 根据应用id, 查询中间件列表
     *
     * @param applicationId 应用id
     * @return 中间件列表
     */
    List<ApplicationMiddlewareListResult> selectByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 批量插入应用中间件
     *
     * @param applicationMiddlewares 中间件列表
     * @return 是否成功
     */
    int insertBatch(@Param("applicationMiddlewares") List<ApplicationMiddlewareEntity> applicationMiddlewares);

    /**
     * 根据状态, 查询状态对应的统计
     * 不传筛选的状态列表, 查询全部
     *
     * @param applicationId 应用id
     * @param statusList 状态列表
     * @return 状态对应统计 列表
     */
    List<ApplicationMiddlewareStatusAboutCountResult> selectStatusCountListByApplicationIdAndStatusAndGroupByStatus(
        @Param("applicationId") Long applicationId, @Param("statusList") List<Integer> statusList);

    /**
     * 应用名称列表, 状态列表 查询 状态统计,
     * 根据 应用名称, 状态值分组
     *
     * @param applicationNameList 应用名称列表
     * @param statusList 状态列表
     * @return 状态统计列表
     */
    List<ApplicationMiddlewareStatusAboutCountResult> selectStatusCountByAndGroupByApplicationNameListAndStatusList(
        @Param("applicationNameList") List<String> applicationNameList, @Param("statusList") List<Integer> statusList);
}

