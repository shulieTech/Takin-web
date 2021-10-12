package io.shulie.takin.web.common.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import io.shulie.takin.parent.exception.entity.ExceptionCode;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.RemoteConstant;
import io.shulie.takin.web.common.domain.WebRequest;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.vo.FileWrapperVO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author qianshui
 * @date 2020/5/11 下午2:33
 */
@Component
@Slf4j
public class HttpWebClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate fileRestTemplate;

    public WebResponse request(WebRequest request) {
        validateParam(request);
        HttpEntity<?> entity = null;
        try {
            ResponseEntity<WebResponse> responseEntity = null;
            if (HttpMethod.GET == request.getHttpMethod()) {
                entity = new HttpEntity<>(null, buildHeader(request));
                responseEntity = restTemplate.exchange(concatGetUrl(request.getRequestUrl(), request),
                    request.getHttpMethod(), entity, WebResponse.class);
            } else {
                entity = new HttpEntity<>(request, buildHeader(request));
                responseEntity = restTemplate.exchange(request.getRequestUrl(), request.getHttpMethod(), entity,
                    WebResponse.class);
            }
            fillHeaderTotal(responseEntity.getHeaders());
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("请求http接口异常，request:{}", JsonHelper.bean2Json(request), e);
            throw new TakinWebException(ExceptionCode.HTTP_REQUEST_ERROR, e.getMessage());
        }
    }

    public WebResponse requestFile(FileWrapperVO fileVO) {
        validateParam(fileVO);
        MultiValueMap<String, FileSystemResource> dataMap = new LinkedMultiValueMap<>();
        List<FileSystemResource> resourceList = Lists.newArrayList();
        fileVO.getFile().forEach(data -> resourceList.add(new FileSystemResource(data)));
        dataMap.put("file", resourceList);
        HttpEntity<MultiValueMap<String, FileSystemResource>> entity = new HttpEntity<>(dataMap,
            buildHeader(fileVO));
        ResponseEntity<WebResponse> responseEntity = fileRestTemplate.exchange(fileVO.getRequestUrl(), HttpMethod.POST,
            entity, WebResponse.class);
        return responseEntity.getBody();
    }

    private void validateParam(WebRequest request) {
        /* TODO web向cloud传参
        if (WebPluginUtils.checkUserData() && request.getLicense() == null) {
            throw ApiException.create(500, "license不能为空");
        }
        if (request.getHttpMethod() == null) {
            throw ApiException.create(500, "请求方式");
        }
        */
    }

    private HttpHeaders buildHeader(WebRequest request) {
        HttpHeaders header = new HttpHeaders();
        if (WebPluginUtils.checkUserPlugin()) {
            /* TODO web向cloud传参
            header.add(RemoteConstant.LICENSE_REQUIRED, "true");
            header.add(RemoteConstant.LICENSE_KEY, request.getLicense());
            header.add(RemoteConstant.USER_ID, request.getUserId() + "");
            if (request.getFilterSql() != null) {
                header.add(RemoteConstant.FILTER_SQL, request.getFilterSql());
            }
             */
        }
        return header;
    }

    private String concatGetUrl(String url, WebRequest request) {
        String jsonString = JSON.toJSONString(request);
        Map<String, Object> dataMap = (Map<String, Object>)JSON.parse(jsonString);
        if (dataMap == null || dataMap.size() == 0) {
            return url;
        }
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        dataMap.entrySet().stream().forEach(data -> {
            sb.append("&");
            sb.append(data.getKey());
            sb.append("=");
            sb.append(data.getValue());
        });
        return sb.toString();
    }

    private void fillHeaderTotal(HttpHeaders headers) {
        if (headers == null || headers.size() == 0) {
            return;
        }
        if (!headers.containsKey(RemoteConstant.PAGE_TOTAL_HEADER)) {
            return;
        }
        Response.setHeaders(new HashMap<String, String>(1) {{put(RemoteConstant.PAGE_TOTAL_HEADER, headers.get(RemoteConstant.PAGE_TOTAL_HEADER).get(0));}});
    }
}