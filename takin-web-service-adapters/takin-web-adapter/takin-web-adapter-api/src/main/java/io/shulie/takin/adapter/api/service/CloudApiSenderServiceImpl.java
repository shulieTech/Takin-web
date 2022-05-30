package io.shulie.takin.adapter.api.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.utils.security.MD5Utils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Cloud接口统一发送服务
 *
 * @author 张天赐
 */
@Slf4j
@Service
public class CloudApiSenderServiceImpl implements CloudApiSenderService {

    @Value("${takin.cloud.url:}")
    private String cloudUrl;
    @Value("${takin.cloud.timeout:-1}")
    private int timeout;

    /**
     * 调用CLOUD接口的统一方法-GET
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @param <C>           请求参数类型
     * @return CLOUD接口响应
     */
    @Override
    public <T, C extends ContextExt> T get(String url, C request, TypeReference<T> responseClass) {
        ContextExt context = drawDataTraceContext(request);
        // 组装请求路径
        String requestUrl = cloudUrl + url;
        // 组装URL参数
        String urlQuery = UrlQuery.of(BeanUtil.beanToMap(request, false, true)).build(StandardCharsets.UTF_8);
        if (StrUtil.isNotBlank(urlQuery)) {
            requestUrl += '?' + urlQuery;
        }
        // 发送请求
        return requestApi(context, Method.GET, requestUrl, new byte[0], responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法-POST
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @param <C>           请求参数类型
     * @return CLOUD接口响应
     */
    @Override
    public <T, C extends ContextExt> T post(String url, @NonNull C request, TypeReference<T> responseClass) {
        return requestWithBody(Method.POST, url, request, responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法-PUT
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @param <C>           请求参数类型
     * @return CLOUD接口响应
     */
    @Override
    public <T, C extends ContextExt> T put(String url, @NonNull C request, TypeReference<T> responseClass) {
        return requestWithBody(Method.PUT, url, request, responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法-DELETE
     *
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @param <C>           请求参数类型
     * @return CLOUD接口响应
     */
    @Override
    public <T, C extends ContextExt> T delete(@NonNull String url, @NonNull C request, TypeReference<T> responseClass) {
        return requestWithBody(Method.DELETE, url, request, responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法 - 文件上传
     *
     * @param url           请求路径
     * @param context       数据溯源参数
     * @param fileListName  文件名称
     * @param fileList      文件内容
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @return CLOUD接口响应
     */
    @Override
    public <T> T uploadFile(String url, ContextExt context, String fileListName, List<File> fileList, TypeReference<T> responseClass) {
        // 组装请求路径
        String requestUrl = cloudUrl + url;
        return requestApi(context, Method.POST, requestUrl, fileListName, fileList.toArray(new File[0]), responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法-带请求体请求
     *
     * @param method        请求方法
     * @param url           请求路径
     * @param request       请求参数
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @param <C>           请求参数类型
     * @return CLOUD接口响应
     */
    private <T, C extends ContextExt> T requestWithBody(Method method, @NonNull String url, @NonNull C request, TypeReference<T> responseClass) {
        ContextExt context = drawDataTraceContext(request);
        String requestUrl = "";
        String requestBodyString = "";
        byte[] requestBody;
        try {
            // 组装请求路径
            requestUrl = cloudUrl + url;
            // 组装请求体
            requestBodyString = new ObjectMapper().writeValueAsString(request);
            // 转换请求体
            requestBody = requestBodyString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            log.error("请求Cloud接口失败,POST前置转换失败.\n请求路径:{}.请求参数:{}.", requestUrl, requestBodyString, throwable);
            throw new RuntimeException(throwable.getMessage());
        }
        // 发送请求
        return requestApi(context, method, requestUrl, requestBody, responseClass);
    }

    /**
     * 调用CLOUD接口的统一方法
     *
     * @param context       数据溯源上下文
     * @param method        请求方式
     * @param url           请求路径
     * @param requestBody   请求体
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @return CLOUD接口响应
     * @throws RuntimeException 在网络异常\请求失败的时候抛出异常
     */
    private <T> T requestApi(ContextExt context, Method method, String url, byte[] requestBody, TypeReference<T> responseClass) {
        String responseBody = "";
        try {
            // 组装HTTP请求对象
            HttpRequest request = HttpUtil
                .createRequest(method, url)
                .contentType(ContentType.JSON.getValue())
                .headerMap(getDataTrace(context,url,requestBody), true)
                .body(requestBody);

            // 设置超时时间
            if (timeout > 0) {
                int realTimeout = timeout * (Long.valueOf(DateUnit.SECOND.getMillis()).intValue());
                request.timeout(realTimeout);
            }
            // 监控接口耗时
            long startTime = System.currentTimeMillis();
            responseBody = request.execute().body();
            long endTime = System.currentTimeMillis();
            log.debug("请求Cloud接口耗时:{}\n请求路径:{}\n请求参数:{}\n请求结果:{}",
                (endTime - startTime), url, new String(requestBody), responseBody);
            // 返回接口响应
            T apiResponse = new ObjectMapper().readValue(responseBody, responseClass);
            if (apiResponse == null) {throw new NullPointerException();}
            if (ApiResult.class.equals(apiResponse.getClass())) {
                ApiResult<?> cloudResult = (ApiResult<?>)apiResponse;
                // 接口成功
                if (Boolean.TRUE.equals(cloudResult.isSuccess())) {return apiResponse;}
                else if (cloudResult.getMsg() != null) {throw new RuntimeException(cloudResult.getMsg());}
                // cloud 回传的 error 信息为空
                else {throw new RuntimeException("无法展示更多信息,请参照cloud日志");}
            }
            return apiResponse;
        } catch (Exception e) {
            log.error("请求Cloud接口异常-JSON序列化失败。\n请求路径:{}\n请求参数:{}\n请求结果:{}", url, new String(requestBody), responseBody);
            throw new RuntimeException(e.getMessage());
        } catch (Throwable throwable) {
            log.error("请求Cloud接口异常。\n请求路径:{}\n请求参数:{}\n请求结果:{}", url, new String(requestBody), responseBody, throwable);
            throw throwable;
        }
    }

    /**
     * 调用CLOUD接口的统一方法 - 文件上传
     *
     * @param context       数据溯源上下文
     * @param method        请求方式
     * @param url           请求路径
     * @param fileListName  文件名称
     * @param fileList      文件内容
     * @param responseClass 响应类型
     * @param <T>           响应参数类型
     * @return CLOUD接口响应
     * @throws RuntimeException 在网络异常\请求失败的时候抛出异常
     */
    private <T> T requestApi(ContextExt context, Method method, String url, String fileListName, File[] fileList, TypeReference<T> responseClass) {
        String responseBody = "";
        try {
            Map<String, String> headMap = getDataTrace(context,url,new byte[0]);
            for(File file:fileList){
                try {
                    headMap.put(
                            MD5Utils.getInstance().getMD5(file.getName()),
                            MD5Utils.getInstance().getMD5(file)
                    );
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            // 组装HTTP请求对象
            HttpRequest request = HttpUtil
                .createRequest(method, url)
                .headerMap(headMap, true)
                .form(fileListName, fileList);
            // 设置超时时间
            if (timeout > 0) {
                int realTimeout = timeout * (Long.valueOf(DateUnit.SECOND.getMillis()).intValue());
                request.timeout(realTimeout);
            }
            // 监控接口耗时
            long startTime = System.currentTimeMillis();
            responseBody = request.execute().body();
            long endTime = System.currentTimeMillis();
            log.debug("请求Cloud接口耗时:{}\n请求路径:{}\n请求参数:{}\n请求结果:{}",
                (endTime - startTime), url, StrUtil.format("{}个文件", fileList.length), responseBody);
            // 返回接口响应
            T apiResponse = new ObjectMapper().readValue(responseBody, responseClass);
            if (apiResponse == null) {throw new NullPointerException();}
            if (ApiResult.class.equals(apiResponse.getClass())) {
                ApiResult<?> cloudResult = (ApiResult<?>)apiResponse;
                // 接口成功
                if (Boolean.TRUE.equals(cloudResult.isSuccess())) {return apiResponse;}
                // success == null || success == false
                else if (cloudResult.getMsg() != null) {throw new RuntimeException(cloudResult.getMsg());}
                // cloud 回传的 error 信息为空
                else {throw new RuntimeException("无法展示更多信息,请参照cloud日志");}
            }
            return apiResponse;
        } catch (Exception e) {
            log.error("请求Cloud接口异常-JSON序列化失败。\n请求路径:{}\n请求参数:{}\n请求结果:{}", url, StrUtil.format("{}个文件", fileList.length), responseBody);
            throw new RuntimeException(e.getMessage());
        } catch (Throwable throwable) {
            log.error("请求Cloud接口异常。\n请求路径:{}\n请求参数:{}\n请求结果:{}", url, StrUtil.format("{}个文件", fileList.length), responseBody, throwable);
            throw throwable;
        }
    }

    /**
     * 抽离数据溯源参数
     *
     * @param param 数据溯源参数
     * @return 纯净溯源对象
     */
    private ContextExt drawDataTraceContext(ContextExt param) {
        // 纯净对象
        ContextExt context = new ContextExt(
            param.getUserId(),
            param.getTenantId(),
            param.getEnvCode(),
            param.getFilterSql(),
            param.getUserName(),
            param.getTenantCode(),
            param.getUserAppKey());
        // 清理原来的上下文
        param.clean();
        // 返回对象
        return context;
    }

    // 填充请求头
    public static final String ENV_CODE = "envCode";
    public static final String USER_APP_KEY = "userAppKey";
    public static final String TENANT_APP_KEY = "tenantAppKey";

    /**
     * 获取请求头信息
     *
     * @return 请求头信息
     */
    private Map<String, String> getDataTrace(ContextExt context,String url,byte[] body) {
        Map<String,String> headMap =  new HashMap<String, String>(4) {{
            put(ENV_CODE, context.getEnvCode());
            put(USER_APP_KEY, String.valueOf(context.getUserAppKey()));
            put(TENANT_APP_KEY, String.valueOf(context.getUserAppKey()));
        }};

        //增加签名相关信息-时间戳
        headMap.put("time",System.currentTimeMillis()+"");  //增加暴力破解难度

        /**
         * 计算签名
         * 字段选取：header中ENV_CODE，FILTER_SQL，TENANT_CODE，USER_ID，TENANT_ID，time，另
         *          url，body
         * 结果写入：header
         */
        TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.putAll(headMap);
        treeMap.put("url", url);
        treeMap.put("body",new String(body));

        String signBodyStr = treeMap.toString().replace("null","");
        String md5 = null;
        try {
            md5 = MD5Utils.getInstance().getMD5(signBodyStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //签名写入header
        headMap.put("md5", md5);
        return headMap;
    }
}
