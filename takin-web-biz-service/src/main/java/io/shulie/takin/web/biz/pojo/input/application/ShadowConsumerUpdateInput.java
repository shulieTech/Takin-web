package io.shulie.takin.web.biz.pojo.input.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerUpdateInput {

    @NotNull
    private Long id;

    @NotBlank
    private String topicGroup;

    @NotNull
    private ShadowMqConsumerType type;

    @NotNull
    private Long applicationId;

    /**
     * 是否可用
     */
    private Integer status;

}
