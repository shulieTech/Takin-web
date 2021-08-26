package io.shulie.takin.web.biz.pojo.request.leakverify;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 9:04 下午
 */
@Data
public class LeakVerifyTaskStopRequest {
    /**
     * 引用类型
     */
    private Integer refType;
    /**
     * 引用id
     */
    private Long refId;
}
