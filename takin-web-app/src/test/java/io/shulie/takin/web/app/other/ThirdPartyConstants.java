package io.shulie.takin.web.app.other;

/**
 * @author liuchuan
 * @date 2021/12/22 9:49 上午
 */
public interface ThirdPartyConstants {

    /**
     * 授权目的, 1 登录回调, 2 绑定回调
     */
    int AUTHORIZE_TARGET_LOGIN = 1;

    /**
     * 第三方授权登录 回调地址
     * %s takin域名
     * %d thirdPartyId
     */
    String THIRD_PARTY_URR_CALLBACK_LOGIN = "%s/api/thirdParty/%d/callback";

    /**
     * 第三方授权绑定 回调地址
     * %s takin域名
     * %d thirdPartyId
     * %d userId
     */
    String THIRD_PARTY_URR_CALLBACK_BIND = "%s/api/thirdParty/%d/callback/bind/%d";

    /**
     * clientId占位符
     */
    String PLACEHOLDER_CLIENT_ID = "${clientId}";

    /**
     * clientSecret占位符
     */
    String PLACEHOLDER_CLIENT_SECRET = "${clientSecret}";

    /**
     * redirectUrl占位符
     */
    String PLACEHOLDER_REDIRECT_URL = "${redirectUrl}";

    /**
     * code占位符
     */
    String PLACEHOLDER_CODE = "${code}";

    /**
     * 获取accessToken的请求方式, 1 get, 2 post
     */
    int REQUEST_METHOD_ACCESS_TOKEN = 1;

    /**
     * 默认部门
     */
    String DEFAULT_DEPARTMENT = "默认部门";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "12345678";


    /**
     * 授权登录时, 用户保存的key前缀
     */
    String CACHE_KEY_AUTHORIZE_USER = "authorize:user:%s";

    /**
     * 第三方授权登录创建用户时, 分布式锁
     * 锁住租户id, 用户名称
     */
    String LOCK_KEY_THIRD_PARTY_CREATE_USER = "lock:key:third_party:create_user:%d:%s";

    /**
     * 第三方授权登录创建默认部门时, 分布式锁
     * 锁住租户id, 部门名称hashcode
     */
    String LOCK_KEY_THIRD_PARTY_CREATE_DEPARTMENT = "lock:key:third_party:create_default_department:%d:%d";

    /**
     * 登录方式, 6 授权登录
     */
    int LOGIN_TYPE_AUTHORIZE = 6;

    /**
     * 登录方式, 1 普通登录
     */
    int LOGIN_TYPE_NORMAL = 1;

    /**
     * 共有的第三方, 租户code
     */
    String COMMON_TENANT_CODE = "common";

    /**
     * 共有的第三方, 租户id
     */
    Long COMMON_TENANT_ID = 0L;

}
