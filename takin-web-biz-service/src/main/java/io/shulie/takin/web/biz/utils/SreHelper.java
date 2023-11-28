package io.shulie.takin.web.biz.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.biz.pojo.response.SreResult;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

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
        private final String tenantAppKey = "tenantAppKey";
        private final String envCode = "envCode";
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
         * 事件（此次调用做什么 比如 amdb查询应用节点数据）
         */
        private String eventName;
        /**
         * 若报错指定的异常
         */
        private TakinWebExceptionEnum exception;

        public SreBuilder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public SreBuilder url(String url) {
            this.url = url;
            return this;
        }

        public SreBuilder param(Object param) {
            String str = JSON.toJSONString(param);
            if (str.contains("{") && str.contains("}")) {
                if (!str.contains(tenantAppKey)) {
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, tenantAppKey + " 不能为空！");
                }
                if (!str.contains(envCode)) {
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, envCode + " 不能为空！");
                }
            }
            this.param = param;
            return this;
        }

        public SreBuilder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public SreBuilder exception(TakinWebExceptionEnum exception) {
            this.exception = exception;
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
            Assert.notNull(this.exception, "exception 不能为空！");
            Assert.notNull(this.eventName, "eventName 不能为空！");
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET;
            }
            Assert.notNull(this.httpMethod, "httpMethod 不能为空！");
            String responseEntity = "";
            if (this.httpMethod.equals(HttpMethod.GET)) {
                responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url, this.param));
                this.eventName += "【GET】";
            } else if (this.httpMethod.equals(HttpMethod.POST)) {
                Assert.notNull(this.param, "param 不能为空！");
                responseEntity = HttpClientUtil.sendPost(url, this.param);
                this.eventName += "【POST】";
            }
            this.eventName = "Sre" + this.eventName;
            if (StringUtils.isBlank(responseEntity)) {
                log.error("{}返回为空,请求地址：{}，请求参数：{}，响应体为空", this.eventName, this.url, JSON.toJSONString(this.param));
                throw new TakinWebException(this.exception, this.eventName + "返回为空！");
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
         * @param clazz 需要被转化的VO class
         * @param <T>   实体泛型
         * @return 实体
         */
        public <T> List<T> list(Class<T> clazz) {
            Assert.notNull(this.url, "url 不能为空！");
            Assert.notNull(this.exception, "exception 不能为空！");
            Assert.notNull(this.eventName, "eventName 不能为空！");
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET;
            }
            String responseEntity = "";
            if (this.httpMethod.equals(HttpMethod.GET)) {
                responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url, this.param));
                this.eventName += "【GET】";
            } else if (this.httpMethod.equals(HttpMethod.POST)) {
                Assert.notNull(this.param, "param 不能为空！");
                responseEntity = HttpClientUtil.sendPost(url, this.param);
                this.eventName += "【POST】";
            }
            this.eventName = "AMDB" + this.eventName;
            if (StringUtils.isBlank(responseEntity)) {
                log.info("{}返回为空,请求地址：{}，请求参数：{}", this.eventName, this.url, JSON.toJSONString(this.param));
                return Collections.EMPTY_LIST;
            }
            SreResult<List<T>> amdbResponse = JSONUtil.toBean(responseEntity, new TypeReference<SreResult<List<T>>>() {
            }, true);

            if (amdbResponse == null || !amdbResponse.getIsSuccess()) {
                log.error("{}返回异常,请求地址：{}，请求参数：{}，响应体：{}", this.eventName, this.url, JSON.toJSONString(this.param), responseEntity);
                throw new TakinWebException(this.exception, this.eventName + "返回异常！");
            }

            if (CollectionUtils.isNotEmpty(amdbResponse.getData().getResultData()) && amdbResponse.getIsSuccess()) {
                return JSONArray.parseArray(JSON.toJSONString(amdbResponse.getData().getResultData()), clazz);
            }
            return Collections.EMPTY_LIST;
        }
    }
}
