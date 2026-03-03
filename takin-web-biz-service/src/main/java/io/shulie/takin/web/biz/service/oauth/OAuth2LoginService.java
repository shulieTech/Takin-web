package io.shulie.takin.web.biz.service.oauth;

import io.shulie.takin.web.biz.pojo.response.user.UserLoginResponse;

/**
 * OAuth 2.0 登录服务接口
 *
 * @author takin
 */
public interface OAuth2LoginService {

    /**
     * 构建 OAuth 授权跳转 URL
     * <p>
     * 拼装参数：response_type=code、client_id、redirect_uri、scope、state
     * state 会存入 Redis（TTL 5分钟）用于防 CSRF 校验
     *
     * @param state 客户端传入的 state（可为空，为空时自动生成 UUID）
     * @return 完整的 OAuth 授权 URL
     */
    String buildAuthorizationUrl(String state);

    /**
     * 通过 OAuth 回调的 code 完成登录
     * <p>
     * 流程：
     * 1. 校验 state 防 CSRF
     * 2. 用 code 换取 access_token
     * 3. 用 access_token 获取用户信息（username/email）
     * 4. 在本系统查找已存在的用户（不存在则抛出异常）
     * 5. 生成 xToken 写入 Redis Session，返回登录信息
     *
     * @param code  OAuth 服务器返回的授权码
     * @param state OAuth 服务器回传的 state，用于 CSRF 校验
     * @return 登录响应，包含 xToken 等用户信息
     */
    UserLoginResponse loginByCode(String code, String state);
}
