package io.shulie.takin.web.biz.pojo.request.leakverify;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 5:40 下午
 */
@Data
public class LeakVerifyTaskReportQueryRequest {

    /**
     * 引用类型：0：压测场景 1：业务流程 2：业务活动
     */
    private Integer refType;

    /**
     * 引用id
     */
    private Long refId;

    /**
     * 压测报告id
     */
    private Long reportId;
}
