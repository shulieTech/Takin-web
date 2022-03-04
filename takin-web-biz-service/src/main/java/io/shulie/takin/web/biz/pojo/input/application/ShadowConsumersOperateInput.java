package io.shulie.takin.web.biz.pojo.input.application;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumersOperateInput {

    @NotEmpty
    private List<ShadowConsumerOperateInput> requests;

    @NotNull
    private Boolean enable;

    @NotNull
    private Long applicationId;

    @Validated
    @Data
    public static class ShadowConsumerOperateInput {

        private Long id;

        @NotBlank
        private String topicGroup;

        @NotNull
        private String type;

    }
}
