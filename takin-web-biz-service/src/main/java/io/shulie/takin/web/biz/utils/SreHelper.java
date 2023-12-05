package io.shulie.takin.web.biz.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.biz.pojo.response.SreResult;
import io.shulie.takin.sre.common.result.SreResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.lang.reflect.Type;

/**
 * amdb帮助类
 *
 * @author caijianying
 */
@Slf4j
@NoArgsConstructor
public class SreHelper {

    public static SreBuilder builder() {
        return new SreBuilder();
    }

    public static class SreBuilder {
        /**
         * HTTP请求 默认GET请求
         */
        private HttpMethod httpMethod;
        /**
         * amdb全路径
         */
        private String url;
        /**
         * amdb 入参
         */
        private Object param;
        /**
         * 超时时间
         */
        private Integer timeout = null;

        public SreBuilder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public SreBuilder url(String url) {
            this.url = url;
            return this;
        }

        public SreBuilder param(Object param) {
            this.param = param;
            return this;
        }

        public SreBuilder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * 返回amdb的单个数据实体
         *
         * @param clazz 需要被转化的实体 class
         * @param <T>   实体泛型
         * @return 实体
         */
        public <T> T one(Class<T> clazz) {
            Assert.notNull(this.url, "url 不能为空！");
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET;
            }
            Assert.notNull(this.httpMethod, "httpMethod 不能为空！");
            String responseEntity = "";
            if (this.httpMethod.equals(HttpMethod.GET)) {
                responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url, this.param));
            } else if (this.httpMethod.equals(HttpMethod.POST)) {
                Assert.notNull(this.param, "param 不能为空！");
                responseEntity = HttpClientUtil.sendPost(url, this.param, timeout);
            }
            if (StringUtils.isBlank(responseEntity)) {
                log.error("请求地址：{}，请求参数：{}，响应体为空", this.url, JSON.toJSONString(this.param));
            }
            SreResult<T> sreResult = JSONUtil.toBean(responseEntity, new TypeReference<SreResult<T>>() {
            }, true);

            if (sreResult != null && sreResult.getIsSuccess()) {
                T data = sreResult.getData().getResultData();
                if (data != null) {
                    return JSON.parseObject(JSON.toJSONString(sreResult.getData().getResultData()), clazz);
                }
            }
            return null;
        }

        /**
         * 返回amdb的数据集合
         *
         * @param typeToken 需要被转化的VO class
         * @param <T>   实体泛型
         * @return 实体
         */
        public <T> SreResponse<T> queryList(TypeToken<SreResponse<T>> typeToken) {
            try {
                if (this.httpMethod == null) {
                    this.httpMethod = HttpMethod.GET;
                }
                String responseEntity = "";
                if (this.httpMethod.equals(HttpMethod.GET)) {
                    responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url, this.param));
                } else if (this.httpMethod.equals(HttpMethod.POST)) {
                    Assert.notNull(this.param, "param 不能为空！");
                    responseEntity = HttpClientUtil.sendPost(url, this.param, timeout);
                }
                if (StringUtils.isBlank(responseEntity)) {
                    log.info("请求地址：{}，请求参数：{}", this.url, JSON.toJSONString(this.param));
                    return SreResponse.fail(String.format("请求地址：%s，请求参数：%s", this.url, JSON.toJSONString(this.param)));
                }
                Gson gson = new Gson();
                Type type = typeToken.getType();
                SreResponse<T> sreResponse = gson.fromJson(responseEntity, type);
                if (sreResponse == null || !sreResponse.isSuccess()) {
                    log.error("请求地址：{}，请求参数：{}，响应体：{}", this.url, JSON.toJSONString(this.param), responseEntity);
                    return SreResponse.fail(String.format("请求地址：%s，请求参数：%s，响应体：%s", this.url, JSON.toJSONString(this.param), responseEntity));
                }
                return sreResponse;
            }catch (Exception e) {
                log.error("queryList error",e);
            }
            return null;
        }
    }
}
