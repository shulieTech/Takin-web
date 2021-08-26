package io.shulie.takin.web.biz.pojo.request.application;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumersOperateRequest {

    @NotEmpty
    private List<ShadowConsumerOperateRequest> requests;

    @NotNull
    private Boolean enable;

    @NotNull
    private Long applicationId;

    @Validated
    @Data
    public static class ShadowConsumerOperateRequest {

        private Long id;

        @NotBlank
        private String topicGroup;

        @NotNull
        private ShadowMqConsumerType type;

    }
}
