package io.shulie.takin.web.amdb.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * amdb帮助类
 *
 * @author caijianying
 */
@Slf4j
@NoArgsConstructor
public class AmdbHelper {

    public static AmdbBuilder builder() {
        return new AmdbBuilder();
    }

    public static class AmdbBuilder {
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
         * 事件（此次调用做什么 比如 amcb查询应用节点数据）
         */
        private String eventName;
        /**
         * 若报错指定的异常
         */
        private TakinWebExceptionEnum exception;

        public AmdbBuilder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public AmdbBuilder url(String url) {
            this.url = url;
            return this;
        }

        public AmdbBuilder param(Object param) {
            final String str = JSON.toJSONString(param);
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

        public AmdbBuilder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public AmdbBuilder exception(TakinWebExceptionEnum exception) {
            this.exception = exception;
            return this;
        }

        /**
         * 返回amdb的单个数据实体
         *
         * @param clazz 需要被转化的实体 class
         * @param <T>
         * @return
         */
        public <T> AmdbResult<T> one(Class<T> clazz) {
            Assert.notNull(this.url, "url 不能为空！");
            Assert.notNull(this.exception, "exception 不能为空！");
            Assert.notNull(this.eventName, "eventName 不能为空！");
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET;
            }
            Assert.notNull(this.httpMethod, "httpMethod 不能为空！");
            String responseEntity = "";
            if (this.httpMethod.equals(HttpMethod.GET)) {
                responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url,
                    this.param));
                this.eventName += "【GET】";
            } else if (this.httpMethod.equals(HttpMethod.POST)) {
                Assert.notNull(this.param, "param 不能为空！");
                responseEntity = HttpClientUtil.sendPost(url, this.param);
                this.eventName += "【POST】";
            }
            this.eventName = "AMDB" + this.eventName;
            if (StringUtils.isBlank(responseEntity)) {
                log.error("{}返回为空,请求地址：{}，请求参数：{}，响应体为空", this.eventName, this.url, JSON.toJSONString(this.param));
                throw new TakinWebException(this.exception, this.eventName + "返回为空！");
            }
            AmdbResult<T> amdbResponse = JSONUtil.toBean(responseEntity,
                new TypeReference<AmdbResult<T>>() {
                }, true);

            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("{}返回异常,请求地址：{}，响应体：{},{}", this.eventName, this.url, responseEntity,
                    ",\"可通过arthas命令 watch io.shulie.takin.web.amdb.util.AmdbHelper$AmdbBuilder param '{params,"
                        + "returnObj,throwExp}'  -n 5  -x 3  获取参数！\"");
                throw new TakinWebException(this.exception, this.eventName + "返回异常！");
            }
            final T data = amdbResponse.getData();
            if (data != null) {
                amdbResponse.setData(JSON.parseObject(JSON.toJSONString(amdbResponse.getData()), clazz));
            }
            return amdbResponse;
        }

        /**
         * 返回amdb的数据集合
         *
         * @param clazz 需要被转化的VO class
         * @param <T>
         * @return
         */
        public <T> AmdbResult<List<T>> list(Class<T> clazz) {
            Assert.notNull(this.url, "url 不能为空！");
            Assert.notNull(this.exception, "exception 不能为空！");
            Assert.notNull(this.eventName, "eventName 不能为空！");
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET;
            }
            String responseEntity = "";
            if (this.httpMethod.equals(HttpMethod.GET)) {
                responseEntity = (this.param == null ? HttpClientUtil.sendGet(url) : HttpClientUtil.sendGet(url,
                    this.param));
                this.eventName += "【GET】";
            } else if (this.httpMethod.equals(HttpMethod.POST)) {
                Assert.notNull(this.param, "param 不能为空！");
                responseEntity = HttpClientUtil.sendPost(url, this.param);
                this.eventName += "【POST】";
            }
            this.eventName = "AMDB" + this.eventName;
            if (StringUtils.isBlank(responseEntity)) {
                log.error("{}返回为空,请求地址：{}，请求参数：{}", this.eventName, this.url, JSON.toJSONString(this.param));
                throw new TakinWebException(this.exception, this.eventName + "返回为空！");
            }
            AmdbResult<List<T>> amdbResponse = JSONUtil.toBean(responseEntity,
                new TypeReference<AmdbResult<List<T>>>() {
                }, true);

            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("{}返回异常,请求地址：{}，请求参数：{}，响应体：{}", this.eventName, this.url, JSON.toJSONString(this.param),
                    responseEntity);
                throw new TakinWebException(this.exception, this.eventName + "返回异常！");
            }

            if (CollectionUtils.isEmpty(amdbResponse.getData())) {
                log.debug("{}返回状态为成功，但数据为空！amdbUrl={},响应信息：{},{}", eventName, this.url, responseEntity,
                    "可通过arthas命令 watch io.shulie.takin.web.amdb.util.AmdbHelper$AmdbBuilder param '{params,returnObj,"
                        + "throwExp}'  -n 5  -x 3 获取参数！");
            } else {
                final List<T> list = JSONArray.parseArray(JSON.toJSONString(amdbResponse.getData()), clazz);
                amdbResponse.setData(list);
            }

            return amdbResponse;
        }
    }

    /**
     * 用于单元测试调试amdb返回json 排查问题
     *
     * @param resultJson
     * @param clazz
     * @param <T>
     */
    public static <T> void debugList(String resultJson, Class<T> clazz) {
        AmdbResult<List<T>> amdbResponse = JSONUtil.toBean(resultJson,
            new TypeReference<AmdbResult<List<T>>>() {
            }, true);
        log.debug(JSON.toJSONString(amdbResponse));
    }

    /**
     * 用于单元测试调试amdb返回json 排查问题
     *
     * @param resultJson
     * @param clazz
     * @param <T>
     */
    public static <T> void debugOne(String resultJson, Class<T> clazz) {
        AmdbResult<T> amdbResponse = JSONUtil.toBean(resultJson,
            new TypeReference<AmdbResult<T>>() {
            }, true);
        log.debug(JSON.toJSONString(amdbResponse));
        amdbResponse.setData(JSON.parseObject(JSON.toJSONString(amdbResponse.getData()), clazz));
    }

}
