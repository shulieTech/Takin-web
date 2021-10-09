package io.shulie.takin.web.ext.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.ext.api.auth.WebDataAuthExtApi;
import io.shulie.takin.web.ext.api.auth.WebUserAuthExtApi;
import io.shulie.takin.web.ext.api.tenant.WebTenantExtApi;
import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by: hezhongqi
 * @date 2021/8/4 14:42
 */
public class WebPluginUtils {

    /**
     * 默认 userAppkey 解决zk PATH 问题
     */
    public static String USER_APP_KEY = "takin";
    public static Long TENANT_ID = -1L;
    public static String ENV_CODE = "test";
    public static Long USER_ID = -1L;

    private static WebUserExtApi userApi;
    private static WebDataAuthExtApi dataAuthApi;
    private static WebUserAuthExtApi userAuthExtApi;
    private static WebTenantExtApi tenantExtApi;

    static PluginManager pluginManager;

    @Autowired
    public void setPluginManager(PluginManager pluginManager) {
        WebPluginUtils.pluginManager = pluginManager;
        userApi = pluginManager.getExtension(WebUserExtApi.class);
        dataAuthApi = pluginManager.getExtension(WebDataAuthExtApi.class);
        userAuthExtApi = pluginManager.getExtension(WebUserAuthExtApi.class);
        tenantExtApi = pluginManager.getExtension(WebTenantExtApi.class);
    }

    /**
     * 补充 插入 更新 用户数据
     *
     * @param userCommonExt -
     */
    public static void fillUserData(UserCommonExt userCommonExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillUserData(userCommonExt);
        }
    }

    /**
     * 补充查询 权限 用户数据
     *
     * @param queryParamCommonExt -
     */
    public static void fillQueryParam(AuthQueryParamCommonExt queryParamCommonExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillQueryParam(queryParamCommonExt);
        }
    }

    /**
     * 补充 查询结果后的用户数据
     *
     * @param queryResponseCommonExt -
     */
    public static void fillQueryResponse(AuthQueryResponseCommonExt queryResponseCommonExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillQueryResponse(queryResponseCommonExt);
        }
    }

    /**
     * 查询用户数据
     *
     * @param userIds -
     * @return -
     */
    public static Map<Long, UserExt> getUserMapByIds(List<Long> userIds) {
        if (CollectionUtils.isNotEmpty(userIds) && Objects.nonNull(userApi)) {
            return userApi.getUserMapByIds(userIds);
        }
        return Maps.newHashMap();
    }

    /**
     * 查询所有用户数据
     *
     * @return -
     */
    public static List<UserExt> selectAllUser() {
        if (Objects.nonNull(userApi)) {
            return userApi.selectAllUser();
        }
        return Lists.newArrayList();
    }

    /**
     * 根据userAppKey查询用户
     *
     * @param userAppKey userAppKey
     * @return UserExt对象
     */
    public static UserExt getUserByAppKey(String userAppKey) {
        if (StringUtils.isBlank(userAppKey)) {
            return null;
        }
        for (UserExt ext : selectAllUser()) {
            if (userAppKey.equals(ext.getKey())) {
                return ext;
            }
        }
        return null;
    }

    /**
     * 根据userID 查询当前的key
     *
     * @param userId -
     * @return -
     */
    public static String getUserAppKey(Long userId) {
        if (Objects.nonNull(userApi)) {
            return userApi.getUserAppKey(userId);
        }
        // 返回默认值
        return USER_APP_KEY;
    }

    /**
     * 补充用户名称
     *
     * @param userId  用户主键
     * @param userMap 用户信息
     * @return 用户名称
     */
    public static String getUserName(Long userId, Map<Long, UserExt> userMap) {
        if (!userMap.isEmpty() && Objects.nonNull(userId) && Objects.nonNull(
            userMap.get(userId))) {
            return userMap.get(userId).getName();
        }
        return "";
    }

    /**
     * 获取登录账号
     *
     * @return 登录的用户信息
     */
    public static UserExt getUser() {
        if (Objects.nonNull(userApi)) {
            return userApi.getUser();
        }
        return null;
    }





    /**
     * 是否带用户模块
     *
     * @return true/false
     */
    public static Boolean checkUserData() {
        if (userApi != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
        //UserExt user = pluginUtils.queryUserByKey(pluginUtils.getTenantUserKey());
        //if (null == user) {
        //    return Response.fail("未查询到用户相关信息");
        //}

        //UserExt user = null;
        ////TakinRestContext.getUser();
        //if (Objects.isNull(user) || user.getUserType() != 0) {
        //    log.error("用户为空或用户类型非管理员，开启验证任务失败");
        //    return;
        //}
    }

    public List<UserExt> selectDistinctUserAppKey() {
        //userMapper.selectDistinctUserAppKey();
        return Lists.newArrayList();
    }

    public static UserExt queryUserFromCache() {
        //if (StringUtils.isBlank(TakinRestContext.getTenantUserKey())) {
        //    OperationLogContextHolder.ignoreLog();
        //    log.error("tenantUserKey为空，应用注册失败");
        //} else {
        //
        //}
        ///UserCacheResult allUserCache.getCachedUserByKey(TakinRestContext.getTenantUserKey());
        return null;
    }

    //public void fillMiddlewareUserData(AppMiddlewareQuery query) {
    //    //UserExt user = null;
    //    ////TakinRestContext.getUser();
    //    //if (1 == user.getRole()) {
    //    //    query.setUserId(user.getId());
    //    //}
    //}

    public static Class<?> getClassByName(String className) {
        try {
            // 先扫描用户插件
            return Class.forName(className, true, pluginManager.getExtension(WebUserExtApi.class).getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 根据用户名 模糊查询
     *
     * @param userName -
     * @return -
     */
    public static List<UserExt> selectByName(String userName) {
        if (Objects.nonNull(userApi)) {
            return userApi.selectByName(userName);
        }
        return Lists.newArrayList();
    }
    //********************************用户权限模块**********************************//
    /**
     * 权限相关数据
     *
     * @return -
     */
    public static List<Long> getQueryAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getQueryAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> getUpdateAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getUpdateAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> getDeleteAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getDeleteAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> getDownloadAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getDownloadAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> getStartStopAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getStartStopAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> getEnableDisableAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.getEnableDisableAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static boolean validateSuperAdmin() {
        if (Objects.nonNull(dataAuthApi)) {
            return userAuthExtApi.validateSuperAdmin();
        }
        return false;
    }
    //********************************用户权限模块**********************************//


    public static Map<String, String> getSystemInfo() {
        if (Objects.nonNull(userApi)) {
            return userApi.getSystemInfo();
        }
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("租户ID", TENANT_ID + "");
        dataMap.put("租户user-app-key", USER_APP_KEY);
        dataMap.put("用户ID", USER_ID + "");
        dataMap.put("用户user-app-key", USER_APP_KEY);
        return dataMap;
    }

    /**
     * 补充cloud 溯源数据
     *
     * @param traceContextExt 溯源数据对象
     */
    public static void fillCloudUserData(ContextExt traceContextExt) {
        if (Objects.nonNull(userApi)) {
            userApi.fillCloudUserData(traceContextExt);
        }
    }

    /**
     * 根据登录租户查询所有数据
     *
     * @return -
     */
    public static UserExt queryUserByKey() {
        if (Objects.nonNull(userApi)) {
            UserExt user = userApi.queryUserByKey();
            if (null == user) {
                // return ResponseResult.fail("当前用户不存在","请联系控制台");
            }
            return user;
        }
        return null;
    }


    //********************************租户插件模块**********************************//
    /**
     * 获取所有租户信息
     *
     * @return -
     */
    public static List<TenantInfoExt> getTenantInfoList() {
        if (Objects.nonNull(tenantExtApi)) {
            return tenantExtApi.getTenantInfoList();
        }
        return null;
    }
    /**
     * 租户参数传递
     * 转 TenantCommonExt
     * @param source -
     * @param target -
     */
    public static void transferTenantParam(TenantCommonExt source, TenantCommonExt target) {
        target.setUserAppKey(source.getUserAppKey());
        target.setEnvCode(source.getEnvCode());
        target.setTenantId(source.getTenantId());
    }

    /**
     * 租户参数传递 cloud 简单传递
     * 转 ContextExt
     * @param source -
     * @param target -
     */
    public static void transferTenantParam(TenantCommonExt source, ContextExt target) {
        target.setEnvCode(source.getEnvCode());
        target.setTenantId(source.getTenantId());
    }
    //********************************租户插件模块**********************************//

    //********************************http线程上下文模块**********************************//
    /**
     * 返回租户id
     * 租户依赖于用户
     * @return 租户主键
     */
    public static Long getTenantId() {
        if (userApi != null) {
            return userApi.getTenantId();
        }
        return TENANT_ID;
    }

    /**
     * 返回 userAppKey
     *
     * @return userAppKey
     */
    public static String getTenantUserAppKey() {
        if (userApi != null) {
            return userApi.getTenantUserKey();
        }
        // 返回一个默认
        return USER_APP_KEY;
    }
    /**
     * 返回环境
     *
     * @return 环境代码
     */
    public static String getEnvCode() {
        if (userApi != null) {
            return userApi.getEnvCode();
        }
        return ENV_CODE;
    }

    /**
     * 返回用户id
     *
     * @return 用户主键
     */
    public static Long getUserId() {
        if (userApi != null) {
            if (userApi.getUser() != null) {
                return userApi.getUser().getId();
            }
        }
        return USER_ID;
    }
    //********************************http线程上下文模块**********************************//


    //********************************插件调用模块**********************************//
    /**
     * 返回默认的环境 目前给插件user-module使用
     */
    public static String getDefaultEnvCode() {
        return ENV_CODE;
    }

    /**
     * 前端 根据租户code 判断租户,默认存在 目前给插件user-module使用
     * @param tenantCode
     * @return
     */
    public static Boolean isExistTenantByCode(String tenantCode) {
        return Boolean.TRUE;
    }

    /**
     * agent 根据租户code 判断租户,默认存在 目前给插件user-module使用
     * @param userAppKey
     * @return
     */
    public static Boolean isExistTenantByKey(String userAppKey) {
        return Boolean.TRUE;
    }

    /**
     * 前端 根据租户code 获取租户信息 目前给插件user-module使用
     * @param tenantCode
     * @return
     */
    public static TenantInfoExt getTenantInfoByCode(String tenantCode) {
        return null;
    }

    /**
     * 前端 根据租户userAppKey 获取租户信息 目前给插件user-module使用
     * @param userAppKey
     * @return
     */
    public static TenantInfoExt getTenantInfoByKey(String userAppKey) {
        return null;
    }

    /**
     * 判断租户,默认存在 目前给插件user-module使用
     */
    public static Long getTenantIdByUserAppKey() {
        return TENANT_ID;
    }
    //********************************插件调用模块**********************************//



}
