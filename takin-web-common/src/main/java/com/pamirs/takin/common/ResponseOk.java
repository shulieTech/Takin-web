package com.pamirs.takin.common;

import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 说明: 成功信息返回类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月13日
 */
@Deprecated
public class ResponseOk {

    /**
     * 创建成功信息返回类
     *
     * @param result 返回的结果
     * @return 包含返回结果的成功信息返回类
     * @author shulie
     * @date 2018年5月21日
     */
    public static ResponseEntity<Object> create(Object result) {
        ResponseResult okResponse = new ResponseResult();
        okResponse.setCode(200);
        okResponse.setMessage("succeed");
        okResponse.setData(result);
        return ResponseEntity.ok(okResponse);
    }

    public static ResponseResult result(Object result) {
        ResponseResult okResponse = new ResponseResult();
        okResponse.setCode(200);
        okResponse.setMessage("succeed");
        okResponse.setData(result);
        return okResponse;
    }

    /**
     * 创建成功信息返回类
     *
     * @param key    返回结果的key
     * @param result 返回的结果(字符串)
     * @return 包含返回结果的成功信息返回类
     * @author shulie
     * @date 2018年5月21日
     */
    public static ResponseEntity<Object> create(String key, String result) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(key, result);
        return create(map);
    }

    /**
     * 创建成功信息返回类
     *
     * @param key    返回结果的key
     * @param result 返回的结果(整型)
     * @return 包含返回结果的成功信息返回类
     * @author shulie
     * @date 2018年5月21日
     */
    public static ResponseEntity<Object> create(String key, Integer result) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(key, result);
        return create(map);
    }

    /**
     * 创建成功信息返回类
     *
     * @param key    返回结果的key
     * @param result 返回的结果(长整型)
     * @return 包含返回结果的成功信息返回类
     * @author shulie
     * @date 2018年5月21日
     */
    public static ResponseEntity<Object> create(String key, Long result) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(key, result);
        return create(map);
    }

    /**
     * 响应结果(内部类)
     *
     * @author shulie
     * @version v1.0
     * @date 2018年5月21日
     */
    public static class ResponseResult {

        /**
         * 响应码
         */
        @JsonProperty("code")
        private int code;

        /**
         * 响应信息
         */
        @JsonProperty("message")
        private String message;

        /**
         * 响应结果
         */
        @JsonProperty("data")
        private Object data;

        /**
         * 2018年5月21日
         *
         * @return the code
         * @author shulie
         */
        public int getCode() {
            return code;
        }

        /**
         * 2018年5月21日
         *
         * @param code the code to set
         * @author shulie
         */
        public void setCode(int code) {
            this.code = code;
        }

        /**
         * 2018年5月21日
         *
         * @return the message
         * @author shulie
         */
        public String getMessage() {
            return message;
        }

        /**
         * 2018年5月21日
         *
         * @param message the message to set
         * @author shulie
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * 2018年5月21日
         *
         * @return the data
         * @author shulie
         */
        public Object getData() {
            return data;
        }

        /**
         * 2018年5月21日
         *
         * @param data the data to set
         * @author shulie
         */
        public void setData(Object data) {
            this.data = data;
        }

    }
}
