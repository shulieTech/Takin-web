package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import io.shulie.takin.web.biz.pojo.request.interfaceperformance.ContentTypeVO;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamDetailResponse;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceParamRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/22 4:13 下午
 */
@Component
public class PerformanceDebugUtil {
    /**
     * 参数解析正则表达式
     * /mess/${hello}/${key}/name
     * <p>
     * hello
     * key
     */
    private static final String PARAM_REGEX = "\\$\\{([^}]*)}";
    private static final Pattern java_Pattern = Pattern.compile(PARAM_REGEX);

    /**
     * 获取RestTemplate
     *
     * @return RestTemplate
     */
    public RestTemplate createResultTemplate(boolean redirect, int timeout, ContentTypeVO contentTypeVO) {
        SSLConnectionSocketFactory csf = getSslConnectionSocketFactory();
        RestTemplate restTemplate = null;
        if (redirect) {
            // 重定向
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(3000);
            //客服端发送请求到与目标url建立起连接的最大时间
            httpRequestFactory.setConnectTimeout(3000);
            // 读取超时
            httpRequestFactory.setReadTimeout(timeout);
            HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new DefaultRedirectStrategyImpl())
                    .setSSLSocketFactory(csf)
                    .build();
            httpRequestFactory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(httpRequestFactory);
        } else {
            NoRedirectClientHttpRequestFactory factory = new NoRedirectClientHttpRequestFactory();
            // 读取超时
            factory.setConnectTimeout(3000);
            factory.setReadTimeout(timeout);
            restTemplate = new RestTemplate(factory);
        }

        // 设置编码
        restTemplate.getMessageConverters().set(1,
                new StringHttpMessageConverter("GBK".equalsIgnoreCase(contentTypeVO.getCodingFormat())
                        ? Charset.forName("GBK") : StandardCharsets.UTF_8));
        restTemplate.setErrorHandler(new DefaultRestErrorHandler());
        return restTemplate;
    }

    private SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        return csf;
    }

    @Contract(threading = ThreadingBehavior.IMMUTABLE)
    public static class DefaultRedirectStrategyImpl extends DefaultRedirectStrategy {
        public static final DefaultRedirectStrategyImpl INSTANCE = new DefaultRedirectStrategyImpl();
        /**
         * Redirectable methods.
         */
        private static final String[] REDIRECT_METHODS = new String[]{
                HttpGet.METHOD_NAME,
                HttpPost.METHOD_NAME,
                HttpPut.METHOD_NAME,
                HttpDelete.METHOD_NAME,
        };

        @Override
        protected boolean isRedirectable(final String method) {
            for (final String m : REDIRECT_METHODS) {
                if (m.equalsIgnoreCase(method)) {
                    return true;
                }
            }
            return false;
        }
    }

    public class NoRedirectClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            connection.setInstanceFollowRedirects(false);
        }
    }

    public class DefaultRestErrorHandler implements ResponseErrorHandler {
        /**
         * 判断返回结果response是否是异常结果
         * 主要是去检查response 的HTTP Status
         * 仿造DefaultResponseErrorHandler实现即可
         */
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            int rawStatusCode = response.getRawStatusCode();
            HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
            return (statusCode != null ? statusCode.isError() : hasError(rawStatusCode));
        }

        protected boolean hasError(int unknownStatusCode) {
            HttpStatus.Series series = HttpStatus.Series.valueOf(unknownStatusCode);
            return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            // 里面可以实现你自己遇到了Error进行合理的处理
        }
    }

    /**
     * 构建请求头
     *
     * @return 请求头
     */
    public HttpHeaders buildHeader(String headers, String cookies, ContentTypeVO contentTypeVO) {
        HttpHeaders header = new HttpHeaders();
        // 请求头
        header.set("Content-Type", getContentType(contentTypeVO));
        addHeader(headers, header);
        addHeader(cookies, header);
        return header;
    }

    private void addHeader(String data, HttpHeaders header) {
        //读取行
        BufferedReader bf = new BufferedReader(new StringReader(StringUtils.isBlank(data) ? "" : data));
        String line = "";
        try {
            while ((line = bf.readLine()) != null) {
                if (StringUtils.isNotBlank(line) && line.contains(":")) {
                    String[] temp = line.split(":");
                    if (temp.length > 1) {
                        if (!("User-Agent".equals(temp[0].trim()) || "p-pradar-debug".equals(temp[0].trim()))) {
                            header.add(temp[0].trim(), temp[1].trim());
                        }
                    }
                }
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(ContentTypeVO vo) {
        if (ContentTypeVO.X_WWW_FORM_URLENCODED.equals(vo.getRadio())) {
            return MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        } else {
            return vo.getType();
        }
    }

    /**
     * 获取匹配的group
     *
     * @param inputStr
     * @return
     */
    public List<String> generateJavaPattern(String inputStr) {
        List<String> groups = Lists.newArrayList();
        try {
            Matcher matcher = java_Pattern.matcher(inputStr);
            while (matcher.find()) {
                // 标记不要了后面拼接上${key}->key
                groups.add(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * 替换基础参数，主要是url
     *
     * @param fileIdDataMap
     * @param regexStr      需要替换的字符串
     * @return
     */
    public String generateBasicResult(Map<String, Map<String, List<Object>>> fileIdDataMap,
                                      String regexStr,
                                      Integer valueIndex,
                                      PerformanceParamDetailResponse detailResponse) {
        List<String> groups = this.generateJavaPattern(regexStr);
        // 无需匹配，直接返回
        if (CollectionUtils.isEmpty(groups)) {
            return regexStr;
        }
        // 遍历替换
        for (int groupIdx = 0; groupIdx < groups.size(); groupIdx++) {
            String tmpStr = "";
            // 获取匹配的字符
            String group = groups.get(groupIdx);
            // 获取当前匹配字符对应的文件Id
            List<PerformanceParamRequest> params = detailResponse.getParamList();
            List<PerformanceParamRequest> groupRequests = params.stream().filter(param -> param.getParamName().equals(group)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(groupRequests)) {
                continue;
            }
            // 默认获取第一个，也只有一个
            Long fileId = groupRequests.get(0).getFileId();
            Map<String, List<Object>> dataMap = fileIdDataMap.get(String.valueOf(fileId));
            for (Map.Entry<String, List<Object>> paramEntry : dataMap.entrySet()) {
                String paramKey = paramEntry.getKey();
                List<Object> paramValues = paramEntry.getValue();
                if (group.equals(paramKey)) {
                    if (CollectionUtils.isNotEmpty(paramValues)) {
                        if (valueIndex >= paramValues.size()) {
                            // 超过了获取的值的索引位置
                            valueIndex = valueIndex % paramValues.size();
                        }
                        // 获取对应位点数据
                        tmpStr = String.valueOf(paramValues.get(valueIndex));
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(tmpStr)) {
                // group没有了${}，拼接以后替换
                regexStr = regexStr.replaceAll("\\$\\{" + group + "}", tmpStr);
            }
        }
        return regexStr;
    }
}