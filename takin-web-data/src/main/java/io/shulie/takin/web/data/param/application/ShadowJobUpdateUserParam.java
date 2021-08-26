package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/5 4:31 下午
 */
@Data
public class ShadowJobUpdateUserParam {
    private Long applicationId;
    private Long userId;

    public ShadowJobUpdateUserParam() {
    }

    public ShadowJobUpdateUserParam(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
