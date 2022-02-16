package io.shulie.takin.web.app.other;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.util.JsonUtil;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2022/1/25 4:33 下午
 */
public class ThirdPartyTest implements ThirdPartyConstants {

    private static final String CLIENT_ID = "1";
    private static final String CLIENT_SECRET = "xx";
    private static final String CODE = "0537";

    /**
     * 测试错误信息
     */
    @Test
    public void testErrorResult() {
        String checkResultJson = "{\n"
            + "\t\"key\": \"error\",\n"
            + "\t\"type\": \"array\",\n"
            + "\t\"expect\": \"\"\n"
            + "}";

        String responseJson = "{\n"
            + "\t\"error\": [\"wrong\", \"invalid\"]\n"
            + "}";


    }

    @Data
    private static class CheckResult {
        private String key;
        private String type;
        private String expect;
    }


    /**
     * 测试入参
     */
    @Test
    public void testAccessTokenParams() {
        String urlTokenParams = "{\"client_id\": \"${clientId}\", \"client_secret\": \"${clientSecret}\", \"code\": "
            + "\"${code}\"}\n";
        urlTokenParams = urlTokenParams.replace(PLACEHOLDER_CLIENT_ID, CLIENT_ID)
            .replace(PLACEHOLDER_CLIENT_SECRET, CLIENT_SECRET)
            .replace(PLACEHOLDER_CODE, CODE);

        // 测试入参
        Map<String, Object> urlTokenParamsMap = JsonUtil.json2Bean(urlTokenParams, Map.class);
        Assert.assertEquals(CLIENT_ID, urlTokenParamsMap.get("client_id").toString());
        Assert.assertEquals(CLIENT_SECRET, urlTokenParamsMap.get("client_secret").toString());
        Assert.assertEquals(CODE, urlTokenParamsMap.get("code").toString());
    }

    /**
     * 测试url
     */
    @Test
    public void testAccessTokenUrl() {
        // 测试url
        int urlTokenMethod = 1;
        String urlTokenParams = "{\"client_id\": \"${clientId}\", \"client_secret\": \"${clientSecret}\", \"code\": "
            + "\"${code}\"}\n";
        urlTokenParams = urlTokenParams.replace(PLACEHOLDER_CLIENT_ID, CLIENT_ID)
            .replace(PLACEHOLDER_CLIENT_SECRET, CLIENT_SECRET)
            .replace(PLACEHOLDER_CODE, CODE);

        Map<String, Object> urlTokenParamsMap = JsonUtil.json2Bean(urlTokenParams, Map.class);

        String urlToken = "www.baidu.com";
        if (REQUEST_METHOD_ACCESS_TOKEN == urlTokenMethod) {
            String urlParams = urlTokenParamsMap.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
            String newUrlToken = String.format("%s?%s", urlToken, urlParams);
            Assert.assertEquals(newUrlToken, "www.baidu.com?code=0537&client_secret=xx&client_id=1");
        }

        urlTokenMethod = 2;
        if (REQUEST_METHOD_ACCESS_TOKEN != urlTokenMethod) {
            Assert.assertEquals(urlToken, "www.baidu.com");
        }
    }

    /**
     * 占位符测试
     */
    @Test
    public void testPlaceHolder() {
        String a = "client_id=${clientId}&redirect_url=${redirectUrl}&client_secret=${clientSecret}&code=${code}";
        a = a.replace(PLACEHOLDER_CLIENT_ID, CLIENT_ID).replace(PLACEHOLDER_CLIENT_SECRET, CLIENT_SECRET)
            .replace(PLACEHOLDER_CODE, CODE).replace(PLACEHOLDER_REDIRECT_URL, "www.baidu.com");
        Assert.assertEquals(a, "client_id=1&redirect_url=www.baidu.com&client_secret=xx&code=0537");
    }

    /**
     * 回调地址测试
     *
     * @throws UnsupportedEncodingException encode抛出的异常
     */
    @Test
    public void testCallbackUrl() throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1L);
        jsonObject.put("isCallbackUnicode", 0);

        // 授权登录回调
        int target = 1;
        String callbackUrl = this.getCallbackUrl(jsonObject, target, null);
        Assert.assertEquals(callbackUrl, "http://www.home.com/api/thirdParty/1/callback");

        jsonObject.put("isCallbackUnicode", 1);
        callbackUrl = this.getCallbackUrl(jsonObject, target, null);
        Assert.assertEquals(callbackUrl, URLEncoder.encode("http://www.home.com/api/thirdParty/1/callback", "UTF-8"));

        // 授权绑定回调
        target = 2;
        callbackUrl = this.getCallbackUrl(jsonObject, target, 7L);
        Assert.assertEquals(callbackUrl, URLEncoder.encode("http://www.home.com/api/thirdParty/1/callback/bind/7", "UTF-8"));

        jsonObject.put("isCallbackUnicode", 0);
        callbackUrl = this.getCallbackUrl(jsonObject, target, 7L);
        Assert.assertEquals(callbackUrl, "http://www.home.com/api/thirdParty/1/callback/bind/7");

    }

    /**
     * 获取回调地址
     *
     * @param thirdParty 第三方详情
     * @param target     授权目的, 1 登录, 2 绑定
     * @param userId     用户id, 登录不需要
     * @return 回调地址
     */
    private String getCallbackUrl(JSONObject thirdParty, Integer target, Long userId) {
        String domain = "http://www.home.com";
        String callbackUrl;
        if (AUTHORIZE_TARGET_LOGIN == target) {
            // 授权登录回调
            callbackUrl = String.format(THIRD_PARTY_URR_CALLBACK_LOGIN, domain, thirdParty.getLong("id"));
        } else {
            // 授权绑定回调
            callbackUrl = String.format(THIRD_PARTY_URR_CALLBACK_BIND, domain, thirdParty.getLong("id"), userId);
        }

        // callback 是否编码
        if (AppConstants.YES == thirdParty.getInteger("isCallbackUnicode")) {
            try {
                callbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("url编码失败, 请重试! 错误信息: " + e.getMessage(), e);
            }
        }

        return callbackUrl;
    }

}
