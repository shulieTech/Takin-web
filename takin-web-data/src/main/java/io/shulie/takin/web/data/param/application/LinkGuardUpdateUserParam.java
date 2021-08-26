package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/5 4:27 下午
 */
@Data
public class LinkGuardUpdateUserParam {
    private Long applicationId;
    private Long userId;

    public LinkGuardUpdateUserParam() {
    }

    public LinkGuardUpdateUserParam(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
