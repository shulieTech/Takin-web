package io.shulie.takin.web.biz.pojo.input.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/19 11:33 上午
 */
@Data
public class ShadowConsumerUpdateUserInput {
    private Long applicationId;
    private Long userId;

    public ShadowConsumerUpdateUserInput() {
    }

    public ShadowConsumerUpdateUserInput(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
