package io.shulie.takin.web.biz.pojo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangz
 * Created on 2023/11/28 19:28
 * Email: zz052831@163.com
 */

@Data
public class SreResult<T> {
    private SreData<T> data;
    private String errorMsg;
    private Boolean isSuccess;

    @Data
    public static class SreData<T> {
        private T resultData;
        private Long taskId;
    }

}
