package io.shulie.takin.web.ext.api.user;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;
import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.SwitchTenantExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantConfigExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoConfigExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;

/**
 * @author hezhongqi
 * @date 2021/7/29 20:30
 */
public interface WebUserExtApi extends ExtensionPoint {
    //********************************用户插件模块**********************************//

    /**
     * 根据userId 获取用户信息
     * @param userId
     * @return
     */
    UserExt getUserExtByUserId(Long userId);


    /**
     * 填充用户数据 简单补充用户数据
     * 用于：
     * 1.mybatis 拦截补充用户数据，插入 + 更新
     * 2.也可用于补充逻辑上的用户数据
     *
     * @param userCommonExt -
     */
    void fillUserData(UserCommonExt userCommonExt);

    /**
     * 填充查询参数
     * 用于：query
     * 1.补充查询条件中用户数据
     *
     * @param queryParamCommonExt -
     */
    void fillQueryParam(AuthQueryParamCommonExt queryParamCommonExt);


    /**
     * 填充返回参数
     * 用于：查询结果补充用户数据
     * 1.补充查询结果中用户数据
     * 2.补充查询结果中按钮权限的数据
     *
     * @param queryResponseCommonExt -
     */
    void fillQueryResponse(AuthQueryResponseCommonExt queryResponseCommonExt);

    /**
     * 根据用户ids 获取应用
     * 用于：查询用户数据
     * todo :或许想办法如何废弃
     *
     * @param userIds -
     * @return -
     */
    Map<Long, UserExt> getUserMapByIds(List<Long> userIds);

    /**
     * 查询所有用户
     * 用于：初始化白名单文件
     *
     * @return -
     */
    List<UserExt> selectAllUser();

    //********************************用户插件模块**********************************//


    /**
     * 根据userid 查询 key信息
     *
     * @return -
     */
    String getUserAppKey(Long userId);

    /**
     * cloud 模块 溯源数据 补充，用于调用cloud接口
     *
     * @param traceContextExt 溯源数据对象
     */
    void fillCloudUserData(ContextExt traceContextExt);

    /**
     * 获取系统个人信息
     *
     * @return -
     */
    Map<String, String> getSystemInfo();

    /**
     * 根据用户名 模糊查询
     *
     * @param userName -
     * @return -
     */
    List<UserExt> selectByName(String userName);


    //********************************http线程上下文模块**********************************//

    /**
     * 设置租户信息
     * @param commonExt
     */
    void setTraceTenantContext(TenantCommonExt commonExt);

    /**
     * 移除租户信息
     */
     void removeTraceContext();


    /**
     * 获取登录用户
     *
     * @return -
     */
    UserExt traceUser();
    /**
     * 获取登录用户的租户key
     *
     * @return -
     */
    String traceTenantAppKey();
    /**
     * 获取登录用户的租户id
     *
     * @return -
     */
    Long traceTenantId();
    /**
     * 获取登录用户的环境
     *
     * @return -
     */
    String traceEnvCode();

    /**
     * 获取租户code
     *
     * @return
     */
    String traceTenantCode();

    //********************************http线程上下文模块**********************************//


    /**
     * 获取企业版默认租户
     * @return
     */
    List<TenantInfoExt> getTenantInfoList();

    /**
     * 根据userappkey获取用户信息
     * @param userAppKey
     * @return
     */
    UserExt queryUserFromCache(String userAppKey);

    /**
     * 切换租户
     * @param ext
     * @return  TenantInfoExt
     */
    TenantInfoExt switchTenant(SwitchTenantExt ext);

    /**
     * 切换环境
     * @param ext
     */
    void switchEnv(SwitchTenantExt ext);

    /**
     * 获取租户配置
     * @return
     */
    List<TenantConfigExt> getTenantConfig();

    /**
     * 获取所有租户的配置
     * @return
     */
    List<TenantInfoConfigExt> getAllTenantConfig();
}
