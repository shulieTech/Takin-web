package io.shulie.takin.adapter.api.service;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * Cloud接口统一发送服务 - 接口
 *
 * @author 张天赐
 */
public interface CloudApiSenderService {

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
    <T, C extends ContextExt> T get(String url, C request, TypeReference<T> responseClass);

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
    <T, C extends ContextExt> T post(String url, C request, TypeReference<T> responseClass);

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
    <T, C extends ContextExt> T put(String url, C request, TypeReference<T> responseClass);

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
    <T, C extends ContextExt> T delete(String url, C request, TypeReference<T> responseClass);

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
    <T> T uploadFile(String url, ContextExt context, String fileListName, List<File> fileList, TypeReference<T> responseClass);
}
