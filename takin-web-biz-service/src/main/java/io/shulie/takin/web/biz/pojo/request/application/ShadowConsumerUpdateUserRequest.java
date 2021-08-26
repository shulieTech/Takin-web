package io.shulie.takin.web.biz.pojo.request.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/19 11:33 上午
 */
@Data
public class ShadowConsumerUpdateUserRequest {
    private Long applicationId;
    private Long userId;

    public ShadowConsumerUpdateUserRequest() {
    }

    public ShadowConsumerUpdateUserRequest(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
