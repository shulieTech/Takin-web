package io.shulie.takin.web.biz.service.oauth.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import io.shulie.takin.web.biz.constant.LoginConstant;
import io.shulie.takin.web.biz.pojo.response.user.UserLoginResponse;
import io.shulie.takin.web.biz.service.oauth.OAuth2LoginService;
import io.shulie.takin.web.common.config.OAuth2Properties;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.mapper.mysql.PradarUserLoginMapper;
import io.shulie.takin.web.data.model.mysql.PradarUserLoginEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth 2.0 登录服务实现
 *
 * @author takin
 */
@Slf4j
@Service
public class OAuth2LoginServiceImpl implements OAuth2LoginService {

    /**
     * Redis key 前缀：OAuth state 防 CSRF
     */
    private static final String OAUTH_STATE_KEY_PREFIX = "takin:oauth:state:";

    /**
     * state 在 Redis 中的有效期（分钟）
     */
    private static final long STATE_EXPIRE_MINUTES = 5L;

    /**
     * Session（xToken）有效期（秒），默认 24 小时
     */
    private static final long SESSION_EXPIRE_SECONDS = 86400L;

    @Autowired
    private OAuth2Properties oAuth2Properties;

    @Autowired
    private RedisClientUtil redisClientUtil;

    @Autowired
    private PradarUserLoginMapper pradarUserLoginMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String buildAuthorizationUrl(String state) {
        // state 为空时自动生成
        if (StringUtils.isBlank(state)) {
            state = UUID.randomUUID().toString().replace("-", "");
        }

        // 将 state 存入 Redis，TTL 5分钟，防 CSRF 攻击
        String stateKey = OAUTH_STATE_KEY_PREFIX + state;
        redisClientUtil.setString(stateKey, "1", (int) STATE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        try {
            String encodedRedirectUri = URLEncoder.encode(oAuth2Properties.getRedirectUri(),
                StandardCharsets.UTF_8.name());
            String encodedScope = URLEncoder.encode(oAuth2Properties.getScope(), StandardCharsets.UTF_8.name());

            return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                oAuth2Properties.getAuthorizationUrl(),
                oAuth2Properties.getClientId(),
                encodedRedirectUri,
                encodedScope,
                state);
        } catch (Exception e) {
            log.error("OAuth2LoginServiceImpl#buildAuthorizationUrl --> URL编码失败, state={}, 错误信息: {}",
                state, e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "构建OAuth授权URL失败: " + e.getMessage());
        }
    }

    @Override
    public UserLoginResponse loginByCode(String code, String state) {
        // 1. 校验 state，防止 CSRF 攻击
        validateState(state);

        // 2. 用 code 换取 access_token
        String accessToken = fetchAccessToken(code);

        // 3. 用 access_token 获取用户信息
        Map<String, Object> userInfo = fetchUserInfo(accessToken);
        String username = resolveUsername(userInfo);

        log.info("OAuth2LoginServiceImpl#loginByCode --> OAuth用户信息获取成功, username={}", username);

        // 4. 查找本系统已存在的用户（仅限已有用户，不自动创建）
        PradarUserLoginEntity userEntity = findExistingUser(username);

        // 5. 生成 xToken，写入 Redis Session
        String xToken = UUID.randomUUID().toString().replace("-", "");
        String sessionKey = LoginConstant.REDIS_KEY_PREFIX + ":" + xToken;
        redisClientUtil.setString(sessionKey, JSON.toJSONString(buildSessionData(userEntity, xToken)),
            (int) SESSION_EXPIRE_SECONDS, TimeUnit.SECONDS);

        log.info("OAuth2LoginServiceImpl#loginByCode --> OAuth登录成功, username={}, xToken={}", username, xToken);

        // 6. 构建并返回登录响应
        return buildLoginResponse(userEntity, xToken);
    }

    // ============================= 私有方法 =============================

    /**
     * 校验 state，防止 CSRF 攻击
     */
    private void validateState(String state) {
        if (StringUtils.isBlank(state)) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "OAuth state 不能为空，请重新发起授权");
        }
        String stateKey = OAUTH_STATE_KEY_PREFIX + state;
        String stateValue = redisClientUtil.getString(stateKey);
        if (StringUtils.isBlank(stateValue)) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                "OAuth state 已过期或无效，请重新发起授权登录");
        }
        // 使用后立即删除，防止重放
        redisClientUtil.delete(stateKey);
    }

    /**
     * 用 code 换取 access_token
     */
    private String fetchAccessToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("code", code);
            params.add("client_id", oAuth2Properties.getClientId());
            params.add("client_secret", oAuth2Properties.getClientSecret());
            params.add("redirect_uri", oAuth2Properties.getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                oAuth2Properties.getTokenUrl(), request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("OAuth2LoginServiceImpl#fetchAccessToken --> 换取token失败, statusCode={}",
                    response.getStatusCode());
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "OAuth 换取 access_token 失败");
            }

            JSONObject tokenResponse = JSON.parseObject(response.getBody());

            // 检查是否有 error 字段
            if (StringUtils.isNotBlank(tokenResponse.getString("error"))) {
                String errorDesc = tokenResponse.getString("error_description");
                log.error("OAuth2LoginServiceImpl#fetchAccessToken --> OAuth服务器返回错误: {}", errorDesc);
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                    "OAuth 授权失败: " + errorDesc);
            }

            String accessToken = tokenResponse.getString("access_token");
            if (StringUtils.isBlank(accessToken)) {
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                    "OAuth 返回的 access_token 为空，请检查 OAuth 服务配置");
            }

            return accessToken;

        } catch (TakinWebException e) {
            throw e;
        } catch (Exception e) {
            log.error("OAuth2LoginServiceImpl#fetchAccessToken --> 请求token端点异常, 错误信息: {}", e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                "请求 OAuth token 端点失败: " + e.getMessage());
        }
    }

    /**
     * 用 access_token 获取用户信息
     */
    private Map<String, Object> fetchUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                oAuth2Properties.getUserInfoUrl(), HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("OAuth2LoginServiceImpl#fetchUserInfo --> 获取用户信息失败, statusCode={}",
                    response.getStatusCode());
                throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "OAuth 获取用户信息失败");
            }

            JSONObject userInfoJson = JSON.parseObject(response.getBody());
            Map<String, Object> userInfo = new HashMap<>(userInfoJson.size());
            userInfo.putAll(userInfoJson);
            return userInfo;

        } catch (TakinWebException e) {
            throw e;
        } catch (Exception e) {
            log.error("OAuth2LoginServiceImpl#fetchUserInfo --> 请求userinfo端点异常, 错误信息: {}", e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                "请求 OAuth userinfo 端点失败: " + e.getMessage());
        }
    }

    /**
     * 从 OAuth 用户信息中解析用户名
     * 优先取 preferred_username，其次 name，最后 email
     */
    private String resolveUsername(Map<String, Object> userInfo) {
        // 按优先级尝试多个字段
        String[] candidateKeys = {"preferred_username", "name", "login", "email"};
        for (String key : candidateKeys) {
            Object value = userInfo.get(key);
            if (value != null && StringUtils.isNotBlank(value.toString())) {
                return value.toString().trim();
            }
        }
        log.error("OAuth2LoginServiceImpl#resolveUsername --> 无法从OAuth用户信息中解析用户名, userInfo={}",
            JSON.toJSONString(userInfo));
        throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
            "无法从 OAuth 用户信息中获取用户名，请检查 OAuth 服务配置");
    }

    /**
     * 在本系统查找已存在的用户（按 user_name 精确匹配）
     * 仅允许已有用户通过 OAuth 登录，不自动创建账号
     */
    private PradarUserLoginEntity findExistingUser(String username) {
        LambdaQueryWrapper<PradarUserLoginEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PradarUserLoginEntity::getUserName, username)
            .eq(PradarUserLoginEntity::getIsDeleted, false)
            .last("LIMIT 1");

        PradarUserLoginEntity userEntity = pradarUserLoginMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            log.warn("OAuth2LoginServiceImpl#findExistingUser --> 系统中不存在该用户, username={}", username);
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON,
                "用户 [" + username + "] 在系统中不存在，请联系管理员添加账号后再尝试 OAuth 登录");
        }
        return userEntity;
    }

    /**
     * 构建写入 Redis 的 Session 数据
     */
    private Map<String, Object> buildSessionData(PradarUserLoginEntity userEntity, String xToken) {
        Map<String, Object> sessionData = new HashMap<>(8);
        sessionData.put("id", userEntity.getId());
        sessionData.put("name", userEntity.getUserName());
        sessionData.put("xToken", xToken);
        sessionData.put("loginTime", System.currentTimeMillis());
        sessionData.put("loginType", 6); // 6 表示 OAuth 授权登录
        return sessionData;
    }

    /**
     * 构建登录响应
     */
    private UserLoginResponse buildLoginResponse(PradarUserLoginEntity userEntity, String xToken) {
        UserLoginResponse response = new UserLoginResponse();
        response.setId(userEntity.getId());
        response.setName(userEntity.getUserName());
        response.setXToken(xToken);
        response.setExpire(false);
        return response;
    }
}
