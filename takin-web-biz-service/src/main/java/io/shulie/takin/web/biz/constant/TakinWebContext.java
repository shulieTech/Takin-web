package io.shulie.takin.web.biz.constant;


import com.alibaba.ttl.TransmittableThreadLocal;

/**
* @Package io.shulie.takin.web.plugin.user.context
* @ClassName: TakinWebRestContext
* @author hezhongqi
* @description:
* @date 2021/9/26 11:45
*/
public final class TakinWebContext {

    /**
     * 当前登录用户的租户 id
     */
    public static TransmittableThreadLocal<Long> tenantIdThreadLocal = new TransmittableThreadLocal<>();

    public static TransmittableThreadLocal<Boolean> tenantIdSignThreadLocal = new TransmittableThreadLocal<>();

    private static TransmittableThreadLocal<String> tableThreadLocal = new TransmittableThreadLocal<>();

    private TakinWebContext() { /* no instance */ }

    /**
     * 获取租户id
     *
     * @return 租户id
     */
    public static Long getTenantId() {
        Long tenantId = tenantIdThreadLocal.get();
        if (tenantId != null) {
            return tenantId;
        }
        return null;
    }

    /**
     * 设置租户id
     *
     * @return 租户id
     */
    public static void setTenantId(Long tenantId) {
        tenantIdThreadLocal.remove();
        tenantIdThreadLocal.set(tenantId);
    }

    /**
     * 获取登陆租户签名配置
     *
     * @return
     */
    public static Boolean getTenantStatus() {
        Boolean status = tenantIdSignThreadLocal.get();
        if (status != null) {
            return status;
        }
        return false;
    }

    /**
     * 获取登陆租户签名配置
     *
     * @return
     */
    public static void setTenantStatus(Boolean status) {
        tenantIdSignThreadLocal.remove();
        tenantIdSignThreadLocal.set(status);

    }

    public static void setTable(String tableName){
        tableThreadLocal.remove();
        tableThreadLocal.set(tableName);
    }

    public static String getTable(){
        String tableName = tableThreadLocal.get();
        tableThreadLocal.remove();
        return tableName;
    }
}
