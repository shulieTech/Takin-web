package io.shulie.takin.web.biz.pojo.request.leakverify;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/8 2:17 下午
 */
@Data
public class LeakVerifyTaskRunWithoutSaveRequest {
    /**
     * 业务活动id
     */
    @NotNull
    private Long businessActivityId;
}
