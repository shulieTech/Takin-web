package io.shulie.takin.web.biz.pojo.request.leakverify;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/28 9:16 下午
 */
@Data
public class LeakVerifyTaskRunWithSaveRequest {
    /**
     * 引用类型：压测场景、业务流程、业务活动
     */
    @NotNull
    private Integer refType;

    /**
     * 压测场景id、业务流程id、业务活动id
     */
    @NotNull
    private Long refId;

    /**
     * 压测报告id
     */
    private Long reportId;
}
