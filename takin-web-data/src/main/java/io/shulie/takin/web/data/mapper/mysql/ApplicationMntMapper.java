package io.shulie.takin.web.data.mapper.mysql;

import java.util.Map;
import java.util.List;

import io.shulie.takin.web.data.result.application.ApplicationInfo;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.QueryApplicationByUpgradeParam;
import io.shulie.takin.web.data.param.application.QueryApplicationParam;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import io.shulie.takin.web.data.result.application.ApplicationListResultByUpgrade;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.apache.ibatis.annotations.Param;

public interface ApplicationMntMapper extends BaseMapper<ApplicationMntEntity> {

    /**
     * 更新应用节点数
     *
     * @param param 参数集合
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateAppNodeNum(@Param("param") NodeNumParam param, @Param("envCode") String envCode,
        @Param("tenantId") Long tenantId);

    /**
     * 获取租户应用数据
     *
     * @param ext
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ApplicationMntEntity> getAllTenantApp(@Param("ext") List<TenantCommonExt> ext);

    /**
     * 导出用
     *
     * @param appId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    ApplicationMntEntity getApplicationByIdWithInterceptorIgnore(@Param("appId") Long appId);

    /**
     * 查询
     *
     * @param statusList
     * @return
     */
    List<ApplicationMntEntity> getAllApplicationByStatus(@Param("statusList") List<Integer> statusList);

    List<ApplicationMntEntity> getApplicationMntByUserIdsAndKeyword(@Param("userIds") List<Long> userIds,
                                                                    @Param("deptIds") List<Long> deptIds,@Param("keyword") String keyword);

    List<ApplicationMntEntity> getAllApplications();

    /**
     * 修复数据使用
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ApplicationMntEntity> getAllApplicationsWithoutTenant();

    /**
     * 返回id
     *
     * @param names
     * @param tenantId
     * @param envCode
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<String> queryIdsByNameAndTenant(@Param("names") List<String> names,
        @Param("tenantId") Long tenantId, @Param("envCode") String envCode);

    Long queryIdByApplicationName(@Param("applicationName") String applicationName);

    /**
     * 更新 agentVersion
     *
     * @param applicationId
     * @param agentVersion
     * @param pradarVersion
     */
    void updateApplicationAgentVersion(@Param("applicationId") Long applicationId,
        @Param("agentVersion") String agentVersion,
        @Param("pradarVersion") String pradarVersion);

    /**
     * 说明: 根据应用id查询应用名称
     *
     * @param applicationId 应用id
     * @return 应用名称
     * @author shulie
     */
    String selectApplicationName(@Param("applicationId") String applicationId);

    /**
     * 判断同一租户下应用名称是否重复
     *
     * @param tenantId
     * @param applicationName
     * @return
     */
    int applicationExistByTenantIdAndAppName(@Param("tenantId") Long tenantId, @Param("envCode") String envCode,
        @Param("applicationName") String applicationName);

    /**
     * 说明: 当链路应用服务信息列表查询不到时,仅仅只按照应用名称查询应用信息
     *
     * @param applicationName 应用名称
     * @return 应用列表
     * @author shulie
     */
    List<ApplicationMntEntity> queryApplicationList(@Param("applicationName") String applicationName,
        @Param("applicationIds") List<String> applicationIds, @Param("userIds") List<Long> userIds);

    /**
     * 说明: 校验该应用是否已经存在
     *
     * @param applicationName
     * @return 大于0表示该应用已存在, 否则不存在
     * @author shulie
     */
    int applicationExist(@Param("applicationName") String applicationName);

    /**
     * 说明: 根据应用id更新应用信息（需要验证权限）
     *
     * @param tApplicationMnt 应用实体类
     * @author shulie
     */
    void updateApplicationInfo(ApplicationMntEntity tApplicationMnt);

    /**
     * 说明: 根据应用id查询关联的基础链路是否存在
     *
     * @param applicationId 应用id
     * @return 关联的基础链路数量和应用名称
     * @author shulie
     * @date 2018/7/10 12:43
     */
    Map<String, Object> queryApplicationRelationBasicLinkByApplicationId(@Param("applicationId") String applicationId);

    /**
     * 说明: 删除应用信息接口
     *
     * @param applicationIdLists 应用id集合
     * @author shulie
     */
    void deleteApplicationInfoByIds(@Param("applicationIdLists") List<Long> applicationIdLists);

    /**
     * 说明: 根据id列表批量查询应用和白名单信息
     *
     * @param applicationIds 应用id集合
     * @return 应用数据
     * @author shulie
     * @date 2018/11/5 10:30
     */
    List<Map<String, Object>> queryApplicationListByIds(@Param("applicationIds") List<Long> applicationIds);

    /**
     * 说明: 查询应用下拉框数据接口
     *
     * @return 应用列表
     * @author shulie
     */
    List<Map<String, Object>> queryApplicationData();

    /**
     * 说明: 修改应用状态（系统内部使用，不需要验证权限）
     *
     * @param applicationIds 应用id列表
     * @author shulie
     */
    void batchUpdateApplicationStatus(@Param("applicationIds") List<Long> applicationIds,
        @Param("accessStatus") Integer accessStatus);

    /**
     * 说明: 查询缓存失效时间
     *
     * @param applicationId 应用id
     * @return 缓存失效时间
     * @author shulie
     */
    Map<String, Object> queryCacheExpTime(@Param("applicationId") String applicationId);

    /**
     * 说明: 根据应用id和脚本类型查询脚本路径
     *
     * @param applicationId 应用id
     * @param scriptType    脚本类型
     * @return 脚本路径
     * @author shulie
     */
    String selectScriptPath(@Param("applicationId") String applicationId,
        @Param("scriptType") String scriptType);

    /**
     * 获取应用名
     *
     * @param applicationName
     * @return
     */
    String getIdByName(@Param("applicationName") String applicationName);

    /**
     * 查询应用列表
     *
     * @param page  分页参数
     * @param param 筛选条件
     * @return 应用列表
     */
    @InterceptorIgnore(tenantLine = "true")
    IPage<ApplicationListResult> selectApplicationPageByParam(
        @Param("page") IPage<ApplicationMntEntity> page, @Param("param") QueryApplicationParam param);


    List<ApplicationMntEntity> getAllApplicationsByField();


    @InterceptorIgnore(tenantLine = "true")
    IPage<ApplicationListResultByUpgrade> selectApplicationListByUpgrade(
            @Param("page") IPage<ApplicationMntEntity> page, @Param("param") QueryApplicationByUpgradeParam param);

    ApplicationInfo getStatus(@Param("name") String name);
}
