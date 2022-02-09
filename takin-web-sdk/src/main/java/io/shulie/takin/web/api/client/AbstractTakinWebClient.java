package io.shulie.takin.web.api.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.api.constant.RemoteUrls;
import io.shulie.takin.web.api.constant.TakinWebClientConfig;
import io.shulie.takin.web.api.internal.CallerInfo;
import io.shulie.takin.web.api.request.TakinWebRequest;
import io.shulie.takin.web.api.response.ResponseResult;
import io.shulie.takin.web.api.response.UserResponse;
import io.shulie.takin.web.api.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caijianying
 */
@Slf4j
public abstract class AbstractTakinWebClient {

    public String webUrl;

    public String appId;

    public String appSecret;

    /**
     * key -> appId
     * value -> callerInfo
     */
    Map<String, CallerInfo> callRecords = new ConcurrentHashMap<>();

    /**
     * 通用调用方法定义
     *
     * @param webRequest
     * @return
     */
    public abstract String executeWithBody(TakinWebRequest webRequest);

    protected Map getHeadherMap() {
        final CallerInfo callerInfo = callRecords.get(appId);
        if (Objects.isNull(callerInfo)|| isExpire(callerInfo)) {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put(TakinWebClientConfig.USER_NAME, appId);
            requestMap.put(TakinWebClientConfig.PASSWORD, appSecret);
            final LocalDateTime now = LocalDateTime.now();
            //请求登录接口
            final String withBody = HttpClientUtil.sendPost(webUrl +"/"+ RemoteUrls.REMOTE_LOGIN,requestMap,null);
            final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);
            if (!result.getSuccess()) {
                log.error("接口返回异常！请求地址：{}，请求参数:{}", webUrl +"/"+ RemoteUrls.REMOTE_LOGIN, JSON.toJSONString(requestMap));
                throw new RuntimeException("接口返回异常！错误信息：" + withBody);
            }
            final String dataJson = JSON.toJSONString(result.getData());
            final UserResponse response = JSON.parseObject(dataJson, UserResponse.class);
            if (Objects.isNull(response)){
                throw new RuntimeException("接口返回有误！返回数据：" + dataJson);
            }
            final CallerInfo info = new CallerInfo();
            info.setAccessToken(response.getxToken());
            info.setExpireDateTime(now.plusMinutes(115));
            info.setCallerUserId(response.getId());
            info.setCallerUserName(response.getName());
            info.setEnvCode(response.getEnvCode());
            info.setTenantCode(response.getTenantCode());
            callRecords.put(appId,info);
            final Map map = buildHeaderMap(info);
            //租户版 需要请求租户信息接口
            if (response.getTenantCode() != null){
                final String sendGet = HttpClientUtil.sendGet(webUrl + "/" + RemoteUrls.REMOTE_TENANT_INFO, null, map);
                final ResponseResult tenantResult = JSON.parseObject(sendGet, ResponseResult.class);
                if (!tenantResult.getSuccess()) {
                    log.error("接口返回异常！请求地址：{}，请求参数:{}", webUrl +"/"+ RemoteUrls.REMOTE_LOGIN, JSON.toJSONString(map));
                    throw new RuntimeException("接口返回异常！错误信息：" + tenantResult);
                }
                final String teneantDataJson = JSON.toJSONString(tenantResult.getData());
                JSONObject jsonObject = JSON.parseObject(teneantDataJson);
                info.setTenantId(jsonObject.getLong("id"));
                callRecords.put(appId,info);
                return buildHeaderMap(info);
            }
            return map;
        }
        return buildHeaderMap(callerInfo);
    }

    /**
     *
     * @param callerInfo
     * @return true 过期
     */
    private boolean isExpire(CallerInfo callerInfo){
        return callerInfo.getExpireDateTime().compareTo(LocalDateTime.now()) < 0;
    }

    private Map buildHeaderMap(CallerInfo callerInfo){
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put(TakinWebClientConfig.HEADER_ENV_CODE,callerInfo.getEnvCode());
        headerMap.put(TakinWebClientConfig.HEADER_TENANT_CODE,callerInfo.getTenantCode());
        headerMap.put(TakinWebClientConfig.HEADER_XTOKEN,callerInfo.getAccessToken());
        headerMap.put(TakinWebClientConfig.TENANT_ID,callerInfo.getTenantId() == null?"":callerInfo.getTenantId().toString());
        return headerMap;
    }


}
