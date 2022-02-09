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
public class DefaultTakinWebClient extends AbstractTakinWebClient{

    public DefaultTakinWebClient() {

    }

    public DefaultTakinWebClient(String webUrl, String appId, String appSecret) {
        this.webUrl = webUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @Override
    public String executeWithBody(TakinWebRequest webRequest){
        if (this.webUrl == null || this.appId == null || this.appSecret == null){
            throw new RuntimeException("缺少必要参数！webUrl、appId、appSecret");
        }
        final Map headerMap = getHeadherMap();
        final Method method = webRequest.getMethod();
        String responseJson = null;
        if (method.equals(Method.GET)){
            responseJson = HttpClientUtil.sendGet(webRequest.getServerUrl(), webRequest.getRequestData(),headerMap);
        }
        if (method.equals(Method.POST)){
            responseJson = HttpClientUtil.sendPost(webRequest.getServerUrl(), webRequest.getRequestData(),headerMap);
        }

        return responseJson;
    }


}
