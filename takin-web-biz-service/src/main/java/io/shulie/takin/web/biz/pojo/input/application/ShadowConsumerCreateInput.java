package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.annocation.Trimmed.TrimmerType;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerCreateInput {

    @NotBlank
    @Trimmed(value = TrimmerType.SIMPLE)
    private String topicGroup;

    @NotNull
    private String type;

    @NotNull
    private Long applicationId;

    /**
     * 老版本用
     * 是否可用
     */
    private Integer status;

    /**
     * 快速接入版本用
     * 1 消费/ 0 不消费影子topic
     */
    private String shadowconsumerEnable;

}
