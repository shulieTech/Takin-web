package io.shulie.takin.web.entrypoint.controller.oauth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.web.biz.pojo.response.user.UserLoginResponse;
import io.shulie.takin.web.biz.service.oauth.OAuth2LoginService;
import io.shulie.takin.web.common.config.OAuth2Properties;
import io.shulie.takin.web.common.domain.WebResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth 2.0 授权登录接口
 * <p>
 * 此接口无需认证，需在拦截器/安全配置中放行 /api/oauth/**
 *
 * @author takin
 */
@Slf4j
@RestController
@RequestMapping("/api/oauth")
@Api(tags = "OAuth2.0 授权登录")
public class OAuth2Controller {

    @Autowired
    private OAuth2LoginService oAuth2LoginService;

    @Autowired
    private OAuth2Properties oAuth2Properties;

    /**
     * 获取 OAuth 授权跳转 URL
     * <p>
     * 前端调用此接口，获取 OAuth 授权 URL 后自行跳转（或由后端直接重定向）
     *
     * @param state 客户端自定义 state（可选），用于防 CSRF；为空时后端自动生成
     * @return 完整的 OAuth 授权 URL
     */
    @GetMapping("/authorize")
    @ApiOperation(value = "获取 OAuth 授权 URL",
        notes = "返回 OAuth2.0 授权跳转地址，前端获取后跳转至该地址引导用户授权")
    public WebResponse<String> authorize(
        @ApiParam(value = "自定义 state（可选）", required = false)
        @RequestParam(required = false) String state) {
        String authorizationUrl = oAuth2LoginService.buildAuthorizationUrl(state);
        log.info("OAuth2Controller#authorize --> 生成授权URL成功");
        return WebResponse.success(authorizationUrl);
    }

    /**
     * OAuth 授权回调接口
     * <p>
     * OAuth 服务器完成用户授权后，会携带 code 和 state 回调此地址。
     * 后端完成 code 换 token、获取用户信息、Session 写入后，重定向至前端首页。
     * xToken 通过 URL 参数和 Cookie 两种方式返回，前端可按需选择。
     *
     * @param code     OAuth 服务器返回的授权码
     * @param state    OAuth 服务器回传的 state，用于 CSRF 校验
     * @param response HttpServletResponse，用于重定向和写 Cookie
     */
    @GetMapping("/callback")
    @ApiOperation(value = "OAuth 授权回调",
        notes = "OAuth 服务器回调地址，完成登录后重定向至前端首页，携带 xToken")
    public void callback(
        @ApiParam(value = "OAuth 授权码", required = true) @RequestParam String code,
        @ApiParam(value = "CSRF 校验 state", required = false) @RequestParam(required = false) String state,
        HttpServletResponse response) {
        try {
            UserLoginResponse loginResponse = oAuth2LoginService.loginByCode(code, state);
            String xToken = loginResponse.getXToken();

            // 将 xToken 写入 Cookie（HttpOnly，防止 XSS 窃取）
            Cookie cookie = new Cookie("xToken", xToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 与 Session 保持一致，24 小时
            response.addCookie(cookie);

            // 重定向到前端首页，同时在 URL 中携带 xToken（兼容前端从 URL 读取的场景）
            String frontendUrl = oAuth2Properties.getFrontendRedirectUrl();
            if (StringUtils.isBlank(frontendUrl)) {
                frontendUrl = "/";
            }
            String redirectUrl = frontendUrl.contains("?")
                ? frontendUrl + "&xToken=" + xToken
                : frontendUrl + "?xToken=" + xToken;

            log.info("OAuth2Controller#callback --> OAuth登录成功，重定向至前端首页");
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2Controller#callback --> OAuth登录失败, code={}, 错误信息: {}", code, e.getMessage(), e);
            // 登录失败时，重定向到前端登录页并携带错误信息
            try {
                String frontendUrl = oAuth2Properties.getFrontendRedirectUrl();
                if (StringUtils.isBlank(frontendUrl)) {
                    frontendUrl = "/";
                }
                // 登录页通常在根路径，携带 error 提示
                String errorRedirect = frontendUrl.contains("?")
                    ? frontendUrl + "&oauthError=" + encodeErrorMessage(e.getMessage())
                    : frontendUrl + "?oauthError=" + encodeErrorMessage(e.getMessage());
                response.sendRedirect(errorRedirect);
            } catch (Exception ex) {
                log.error("OAuth2Controller#callback --> 重定向失败: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * 对错误信息进行 URL 编码
     */
    private String encodeErrorMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return "OAuth%E7%99%BB%E5%BD%95%E5%A4%B1%E8%B4%A5";
        }
        try {
            return java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return "OAuth%E7%99%BB%E5%BD%95%E5%A4%B1%E8%B4%A5";
        }
    }
}
