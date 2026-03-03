package io.shulie.takin.web.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OAuth 2.0 配置属性
 *
 * @author takin
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

    /**
     * OAuth 应用 clientId
     */
    private String clientId;

    /**
     * OAuth 应用 clientSecret
     */
    private String clientSecret;

    /**
     * OAuth 授权页 URL，用于引导用户授权
     * 例：https://your-oauth-server/authorize
     */
    private String authorizationUrl;

    /**
     * 换取 access_token 的端点 URL
     * 例：https://your-oauth-server/token
     */
    private String tokenUrl;

    /**
     * 获取用户信息的端点 URL
     * 例：https://your-oauth-server/userinfo
     */
    private String userInfoUrl;

    /**
     * OAuth 回调地址（需在 OAuth 服务器注册）
     * 例：http://localhost:10008/takin-web/api/oauth/callback
     */
    private String redirectUri;

    /**
     * 请求的权限范围，空格分隔
     * 例：openid profile email
     */
    private String scope;

    /**
     * OAuth 登录成功后重定向的前端地址
     * 例：http://localhost/
     */
    private String frontendRedirectUrl;
}
