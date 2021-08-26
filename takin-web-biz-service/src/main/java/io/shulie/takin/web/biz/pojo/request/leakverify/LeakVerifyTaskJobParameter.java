package io.shulie.takin.web.biz.pojo.request.leakverify;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/8 2:54 下午
 */
@Data
public class LeakVerifyTaskJobParameter {
    /**
     * 引用类型
     */
    @NotNull
    private Integer refType;
    /**
     * 引用id
     */
    @NotNull
    private Long refId;

    /**
     * 报告id
     */
    private Long reportId;

    /**
     * 验证频率 单位：分钟
     */
    private Integer timeInterval;

    /**
     * 配置列表
     */
    private List<VerifyTaskConfig> verifyTaskConfigList;
}
