package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/23 8:12 下午
 */
@Data
public class ApplicationApiUpdateUserParam {
    private Long applicationId;
    private Long userId;

    public ApplicationApiUpdateUserParam(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
