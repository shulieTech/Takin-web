package io.shulie.takin.web.biz.pojo.request.leakverify;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 8:52 下午
 */
@Data
public class LeakVerifyTaskStartRequest {

    /**
     * 引用类型：压测场景
     */
    @NotNull
    private Integer refType;

    /**
     * 压测场景id
     */
    @NotNull
    private Long refId;

    /**
     * 业务活动id
     */
    @NotEmpty
    private List<Long> businessActivityIds;

    /**
     * 报告id
     */
    @NotNull
    private Long reportId;

    /**
     * 验证频率 单位：分钟
     */
    @NotNull
    private Integer timeInterval;
}
