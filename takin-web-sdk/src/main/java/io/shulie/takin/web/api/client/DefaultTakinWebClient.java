package io.shulie.takin.web.api.client;

import java.util.Map;

import cn.hutool.http.Method;
import io.shulie.takin.web.api.request.TakinWebRequest;
import io.shulie.takin.web.api.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caijianying
 */
@Slf4j
public class DefaultTakinWebClient extends AbstractTakinWebClient {

    public DefaultTakinWebClient() {

    }

    public DefaultTakinWebClient(String webUrl, String appId, String appSecret) {
        this.webUrl = webUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @Override
    public String execute(TakinWebRequest webRequest) {
        if (this.webUrl == null || this.appId == null || this.appSecret == null) {
            throw new IllegalArgumentException("缺少必要参数！webUrl、appId、appSecret");
        }
        if (webRequest == null || webRequest.getServerUrl() == null || webRequest.getRequestData() == null || webRequest.getMethod() == null) {
            throw new IllegalArgumentException("缺少必要参数！serverUrl、requestData、method");
        }
        final Map headerMap = getCallerHeader();
        final Method method = webRequest.getMethod();
        String responseJson = null;
        if (method.equals(Method.GET)) {
            responseJson = HttpClientUtil.sendGet(webRequest.getServerUrl(), webRequest.getRequestData(), headerMap);
        } else if (method.equals(Method.POST)) {
            responseJson = HttpClientUtil.sendPost(webRequest.getServerUrl(), webRequest.getRequestData(), headerMap);
        } else {
            throw new UnsupportedOperationException("http method【" + method + "】 is not supported!");
        }

        return responseJson;
    }

}
