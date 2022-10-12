package io.shulie.takin.web.ext.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.ext.api.agentupgradeonline.AgentHeartbeatExtApi;
import io.shulie.takin.web.ext.api.auth.WebDataAuthExtApi;
import io.shulie.takin.web.ext.api.auth.WebUserAuthExtApi;
import io.shulie.takin.web.ext.api.e2e.InspectionExtApi;
import io.shulie.takin.web.ext.api.tenant.WebTenantExtApi;
import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by: hezhongqi
 * @date 2021/8/4 14:42
 */
@SuppressWarnings("unused")
public class WebPluginUtils {

    /**
     * 默认 userAppKey 解决zk PATH 问题
     */
	public static Long CUSTOMER_ID = -1L;
    public static String DEFAULT_TENANT_APP_KEY = "default";
    public static Long DEFAULT_TENANT_ID = 1L;
    public static String DEFAULT_ENV_CODE = "test";
    public static String DEFAULT_TENANT_CODE = "default";

    /**
     * 默认系统相关属性
     */
    public static String SYS_DEFAULT_ENV_CODE = "system";
    public static Long SYS_DEFAULT_TENANT_ID = -1L;

    public static Long DEFAULT_USER_ID = -1L;

    private static WebUserExtApi userApi;
    private static WebDataAuthExtApi dataAuthApi;
    private static WebUserAuthExtApi userAuthExtApi;
    private static AgentHeartbeatExtApi agentHeartbeatExtApi;
    public static InspectionExtApi inspectionExtApi;
    private static WebTenantExtApi tenantExtApi;

    static PluginManager pluginManager;

    @Autowired
    public void setPluginManager(PluginManager pluginManager) {
        WebPluginUtils.pluginManager = pluginManager;
        userApi = pluginManager.getExtension(WebUserExtApi.class);
        dataAuthApi = pluginManager.getExtension(WebDataAuthExtApi.class);
        userAuthExtApi = pluginManager.getExtension(WebUserAuthExtApi.class);
        agentHeartbeatExtApi = pluginManager.getExtension(AgentHeartbeatExtApi.class);
        inspectionExtApi = pluginManager.getExtension(InspectionExtApi.class);
        tenantExtApi = pluginManager.getExtension(WebTenantExtApi.class);
    }

    @AllArgsConstructor
    @Getter
    public enum EnvCodeEnum {
        /**
         * 测试环境
         */
        TEST("test", "测试环境", ""),
        /**
         * 生产环境
         */
        PROD("prod", "生产环境", "当前环境为生产环境，请谨慎操作");

        private final String envCode;
        private final String envName;
        private final String desc;
    }

    /**
     * 获取 IAgentCommandProcessor 实现类处理不同的心跳指令
     *
     * 由于 IAgentCommandProcessor 类在biz-service模块下所以在当前接口定义中只定义返回Object对象，使用时加下类型判断
     *
     * @return IAgentCommandProcessor子类集合
     */
    public static List<Object> getAgentCommandProcessor() {
        List<Object> result = new ArrayList<>();
        if (Objects.nonNull(agentHeartbeatExtApi)) {
            result = agentHeartbeatExtApi.getAgentCommandProcessor();
        }
        return result;
    }
    //********************************用户插件模块**********************************//

    /**
     * 根据userId 获取 用户信息
     *
     * @param userId 用户主键
     * @return 用户信息
     */
    public static UserExt getUserExtByUserId(Long userId) {
        if (Objects.nonNull(userApi)) {
            return userApi.getUserExtByUserId(userId);
        }
        return null;
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

    //********************************用户插件模块**********************************//

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
            userIds = userIds.stream().filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            Map<Long, UserExt> userMap = userApi.getUserMapByIds(userIds);
            if (null != userMap) {
                return userMap;
            }
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
     * 是否带用户模块
     *
     * @return true/false
     */
    public static Boolean checkUserPlugin() {
        if (userApi != null) {return Boolean.TRUE;}
        return Boolean.FALSE;
    }

    public List<UserExt> selectDistinctUserAppKey() {
        //userMapper.selectDistinctUserAppKey();
        return Lists.newArrayList();
    }

    public static UserExt queryUserFromCache(String userAppKey) {
        if (userApi != null) {
            return userApi.queryUserFromCache(userAppKey);
        }
        return null;
    }

    public static Long traceDeptId(){
        if (userApi != null) {
            return userApi.traceDeptId();
        }
        return null;
    }

    public static Class<?> getClassByName(String className) {
        try {
            // 先扫描用户插件
            return Class.forName(className, true, pluginManager.getExtension(WebUserExtApi.class).getClass()
                .getClassLoader());
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
    public static List<Long> queryAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.queryAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> createAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.createAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> updateAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.updateAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> deleteAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.deleteAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> enableDisableAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.enableDisableAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> startStopAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.startStopAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> downloadAllowUserIdList() {
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.downloadAllowUserIdList();
        }
        return Lists.newArrayList();
    }

    public static List<Long> reportAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.reportAllowUserIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> pigeonholeAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.pigeonholeAllowUserIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> shareAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.shareAllowUserIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> copyAllowUserIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.copyAllowUserIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> queryAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.queryAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> createAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.createAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> updateAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.updateAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> deleteAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.deleteAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> enableDisableAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.enableDisableAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> startStopAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.startStopAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> downloadAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.downloadAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> reportAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.reportAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> pigeonholeAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.pigeonholeAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> shareAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.shareAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
    public static List<Long> copyAllowDeptIdList(){
        if (Objects.nonNull(dataAuthApi)) {
            return dataAuthApi.copyAllowDeptIdList();
        }
        return Lists.newArrayList();
    }
	/**
     * 返回租户id
     *
     * @return 租户主键
     */
    public static Long getCustomerId() {
        if (userApi != null) {
            if (userApi.getUser() != null) {
                return userApi.getUser().getCustomerId();
            }
        }
        return CUSTOMER_ID;
    }

    public static boolean validateAdmin() {
        if (Objects.nonNull(dataAuthApi)) {
            return userAuthExtApi.validateAdmin();
        }
        return false;
    }
    //********************************用户权限模块**********************************//

    //********************************租户插件模块**********************************//

    /**
     * 是否有租户插件
     *
     * @return true/false
     */
    public static Boolean checkTenantPlugin() {
        if (tenantExtApi != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 前端 根据租户code 获取租户信息 目前给插件user-module使用
     *
     * @param tenantAppKey 租户AppKey
     * @param tenantCode   租户编码
     * @return 租户信息
     */
    public static TenantInfoExt getTenantInfo(String tenantAppKey, String tenantCode) {
        if (tenantExtApi != null) {
            return tenantExtApi.getTenantInfo(tenantAppKey, tenantCode);
        }
        if (userApi != null) {
            // 只是带用户插件
            List<TenantInfoExt> tenantInfoList = userApi.getTenantInfoList();
            if (CollectionUtils.isEmpty(tenantInfoList)) {
                return null;
            }
            TenantInfoExt tenantInfoExt = tenantInfoList.get(0);
            if (StringUtils.isNotBlank(tenantAppKey) && tenantAppKey.equals(tenantInfoExt.getTenantAppKey())) {
                return tenantInfoExt;
            }
            if (StringUtils.isNotBlank(tenantCode) && tenantCode.equals(tenantInfoExt.getTenantCode())) {
                return tenantInfoExt;
            }
            return null;
        }
        return null;
    }

    /**
     * 获取租户信息
     *
     * @param tenantId 租户主键
     * @return 租户信息
     */
    public static TenantInfoExt getTenantInfo(Long tenantId) {
        if (tenantExtApi != null) {
            return tenantExtApi.getTenantInfo(tenantId);
        }
        if (userApi != null) {
            // 只是带用户插件
            List<TenantInfoExt> tenantInfoList = userApi.getTenantInfoList();
            if (CollectionUtils.isEmpty(tenantInfoList)) {
                return null;
            }
            return tenantInfoList.get(0);
        }
        return null;
    }

    /**
     * 返回默认的环境 目前给插件user-module使用
     *
     * @param userAppKey 用户AppKey
     * @param tenantCode 租户编码
     * @return 默认环境
     */
    public static String getDefaultEnvCode(String userAppKey, String tenantCode) {
        if (tenantExtApi != null) {
            return tenantExtApi.getDefaultEnvCode(userAppKey, tenantCode);
        }
        return DEFAULT_ENV_CODE;
    }

    /**
     * 获取默认用户id
     *
     * @param userAppKey 用户AppKey
     * @param tenantCode 租户编码
     * @return 默认用户主键
     */
    public static Long getDefaultUserId(String userAppKey, String tenantCode) {
        if (tenantExtApi != null) {
            return tenantExtApi.getDefaultUserId(userAppKey, tenantCode);
        }
        if (userApi != null) {
            return 1L;
        }
        return DEFAULT_USER_ID;
    }

    /**
     * 前端 根据租户code 判断租户,默认存在 目前给插件user-module使用
     *
     * @param tenantAppKey 租户AppKey
     * @param tenantCode   租户编码
     * @return 是否存在租户
     */
    public static Boolean isExistTenant(String tenantAppKey, String tenantCode) {
        if (tenantExtApi != null) {
            return tenantExtApi.isExistTenant(tenantAppKey, tenantCode);
        }
        if (userApi != null) {
            // 只是带用户插件
            List<TenantInfoExt> tenantInfoList = userApi.getTenantInfoList();
            if (CollectionUtils.isEmpty(tenantInfoList)) {
                return Boolean.FALSE;
            }
            TenantInfoExt tenantInfoExt = tenantInfoList.get(0);
            if (tenantAppKey.equals(tenantInfoExt.getTenantAppKey())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 根据租户id查询当前租户 key
     *
     * @param tenantId -
     * @return -
     */

    public static TenantCommonExt fillTenantCommonExt(Long tenantId, String envCode) {
        if (Objects.nonNull(tenantExtApi) && tenantId != null) {
            // saas
            TenantInfoExt tenantInfo = tenantExtApi.getTenantInfo(tenantId);
            if (tenantInfo != null) {
                TenantCommonExt ext = new TenantCommonExt();
                ext.setTenantId(tenantId);
                ext.setEnvCode(envCode);
                ext.setTenantAppKey(tenantInfo.getTenantAppKey());
                return ext;
            }
        }
        if (Objects.nonNull(userApi)) {
            // 企业版
            TenantInfoExt infoExt = userApi.getTenantInfoList().get(0);
            TenantCommonExt ext = new TenantCommonExt();
            ext.setEnvCode(envCode);
            ext.setTenantAppKey(infoExt.getTenantAppKey());
            ext.setTenantId(infoExt.getTenantId());
            ext.setTenantCode(infoExt.getTenantCode());
            return ext;
        }
        // 开源版
        TenantCommonExt ext = new TenantCommonExt();
        ext.setEnvCode(DEFAULT_ENV_CODE);
        ext.setTenantAppKey(DEFAULT_TENANT_APP_KEY);
        ext.setTenantId(DEFAULT_TENANT_ID);
        ext.setTenantCode(DEFAULT_TENANT_CODE);
        return ext;
    }

    /**
     * 获取所有租户信息
     *
     * @return -
     */
    public static List<TenantInfoExt> getTenantInfoList() {
        if (Objects.nonNull(tenantExtApi)) {
            return tenantExtApi.getTenantInfoList();
        }
        // 默认一个租户
        if (Objects.nonNull(userApi)) {
            // 企业版
            return userApi.getTenantInfoList();
        }
        // 开源版本
        return getDefaultTenantInfoList();
    }

    public static Boolean isOpenVersion() {
        return Objects.isNull(tenantExtApi) && Objects.isNull(userApi);
    }

    /**
     * 租户参数传递
     * 转 TenantCommonExt
     *
     * @param source -
     * @param target -
     */
    public static void transferTenantParam(TenantCommonExt source, TenantCommonExt target) {
        target.setTenantAppKey(source.getTenantAppKey());
        target.setEnvCode(source.getEnvCode());
        target.setTenantId(source.getTenantId());
    }

    /**
     * 租户参数传递 cloud 简单传递
     * 转 ContextExt
     *
     * @param source -
     * @param target -
     */
    public static void transferTenantParam(TenantCommonExt source, ContextExt target) {
        target.setEnvCode(source.getEnvCode());
        target.setTenantId(source.getTenantId());
    }

    //********************************租户插件模块**********************************//

    public static Map<String, String> getSystemInfo() {
        if (Objects.nonNull(userApi)) {
            return userApi.getSystemInfo();
        }
        HashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("租户ID", DEFAULT_TENANT_ID + "");
        dataMap.put("租户tenant-app-key", DEFAULT_TENANT_APP_KEY);
        dataMap.put("租户code", DEFAULT_TENANT_CODE);
        dataMap.put("用户ID", DEFAULT_USER_ID + "");
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
        } else {
            traceContextExt.setUserId(DEFAULT_USER_ID);
            traceContextExt.setEnvCode(DEFAULT_ENV_CODE);
            traceContextExt.setTenantId(DEFAULT_TENANT_ID);
            traceContextExt.setTenantCode(DEFAULT_TENANT_CODE);
            traceContextExt.setUserAppKey(DEFAULT_TENANT_APP_KEY);
        }
    }

    /**
     * 设置cloud 溯源数据
     *
     * @param traceContextExt 溯源数据对象
     */
    public static void setCloudUserData(ContextExt traceContextExt) {
        if (Objects.nonNull(userApi)) {
            userApi.setCloudUserData(traceContextExt);
        }
    }

    //********************************http线程上下文模块**********************************//

    /**
     * 设置租户信息
     *
     * @param tenantId     租户 id
     * @param tenantAppKey appKey
     * @param envCode      环境
     */
    public static TenantCommonExt setTraceTenantContext(Long tenantId, String tenantAppKey, String envCode, String tenantCode,
        Integer source) {
        TenantCommonExt tenantCommonExt = null;
        if (Objects.nonNull(userApi)) {
            tenantCommonExt = new TenantCommonExt();
            tenantCommonExt.setTenantId(tenantId);
            tenantCommonExt.setTenantAppKey(tenantAppKey);
            tenantCommonExt.setEnvCode(envCode);
            tenantCommonExt.setTenantCode(tenantCode);
            tenantCommonExt.setSource(source);
            userApi.setTraceTenantContext(tenantCommonExt);
        }
        return tenantCommonExt;
    }

    /**
     * 设置租户信息
     *
     * @param commonExt 溯源信息
     */
    public static void setTraceTenantContext(TenantCommonExt commonExt) {
        if (Objects.nonNull(userApi)) {
            userApi.setTraceTenantContext(commonExt);
        }
    }

    /**
     * 移除租户信息
     */
    public static void removeTraceContext() {
        if (Objects.nonNull(userApi)) {
            userApi.removeTraceContext();
        }
    }

    /**
     * 获取登录账号
     *
     * @return 登录的用户信息
     */
    public static UserExt traceUser() {
        if (Objects.nonNull(userApi)) {
            return userApi.traceUser();
        }
        return null;
    }

    /**
     * 返回租户id
     * 租户依赖于用户
     *
     * @return 租户主键
     */
    public static Long traceTenantId() {
        if (userApi != null) {
            // 未登录
            if (userApi.traceSource() == null) {
                return null;
            }
            Long tenantId = userApi.traceTenantId();
            if (Objects.isNull(tenantId)) {
                throw new RuntimeException("租户ID不能为空！");
            }
            return tenantId;
        }
        return DEFAULT_TENANT_ID;
    }

    /**
     * 返回租户id
     * 租户依赖于用户
     * 系统全局表查询使用
     *
     * @return 租户主键
     */
    public static List<Long> traceTenantIdForSystem() {
        return Lists.newArrayList(SYS_DEFAULT_TENANT_ID, traceTenantId());
    }

    /**
     * 返回 tenantAppKey
     *
     * @return tenantAppKey
     */
    public static String traceTenantAppKey() {
        if (userApi != null) {
            return userApi.traceTenantAppKey();
        }
        // 返回一个默认
        return DEFAULT_TENANT_APP_KEY;
    }

    /**
     * 返回环境
     *
     * @return 环境代码
     */
    public static String traceEnvCode() {
        if (userApi != null) {
            if (userApi.traceSource() == null) {
                return "-1";
            }
            String envCode = userApi.traceEnvCode();
            if (StringUtils.isBlank(envCode)) {
                throw new RuntimeException("环境编码不能为空！");
            }
            return envCode;
        }
        return DEFAULT_ENV_CODE;
    }

    /**
     * -
     *
     * @return -
     */
    public static List<String> traceEnvCodeForSystem() {
        return Lists.newArrayList(SYS_DEFAULT_ENV_CODE, traceEnvCode());
    }

    /**
     * 返回用户id
     *
     * @return 用户主键
     */
    public static Long traceUserId() {
        if (userApi != null) {
            final UserExt user = userApi.traceUser();
            if (user == null) {
                return null;
            }
            return user.getId();
        }
        return DEFAULT_USER_ID;
    }

    /**
     * 返回租户code
     *
     * @return 租户编码
     */
    public static String traceTenantCode() {
        if (userApi != null) {
            return userApi.traceTenantCode();
        }
        return DEFAULT_TENANT_CODE;
    }

    /**
     * 组装 http 租户参数
     *
     * @return 租户参数
     */
    public static TenantCommonExt traceTenantCommonExt() {
        TenantCommonExt ext = new TenantCommonExt();
        ext.setTenantId(traceTenantId());
        ext.setEnvCode(traceEnvCode());
        ext.setTenantAppKey(traceTenantAppKey());
        ext.setTenantCode(traceTenantCode());
        return ext;
    }
    //********************************http线程上下文模块**********************************//

    /**
     * 获取默认租户
     *
     * @return 默认租户列表
     */
    private static List<TenantInfoExt> getDefaultTenantInfoList() {
        List<TenantInfoExt> extList = Lists.newArrayList();
        TenantInfoExt ext = new TenantInfoExt();
        ext.setTenantId(DEFAULT_TENANT_ID);
        ext.setTenantAppKey(DEFAULT_TENANT_APP_KEY);
        ext.setTenantName(DEFAULT_TENANT_APP_KEY);
        ext.setTenantNick(DEFAULT_TENANT_APP_KEY);
        ext.setTenantCode(DEFAULT_TENANT_APP_KEY);
        ext.setEnvs(getDefaultTenantEnvList());
        extList.add(ext);
        return extList;
    }

    /**
     * 获取默认租户 环境
     *
     * @return -
     */
    private static List<TenantEnv> getDefaultTenantEnvList() {
        List<TenantEnv> envs = Lists.newArrayList();
        TenantInfoExt.TenantEnv env = new TenantEnv();
        env.setEnvCode(DEFAULT_ENV_CODE);
        env.setEnvName("测试环境");
        env.setDesc("无插件");
        env.setIsDefault(Boolean.TRUE);
        envs.add(env);
        return envs;
    }
}
