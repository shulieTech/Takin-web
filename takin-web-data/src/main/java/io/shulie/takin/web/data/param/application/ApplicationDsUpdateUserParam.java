package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/2/5 4:19 下午
 */
@Data
public class ApplicationDsUpdateUserParam {
    private Long applicationId;
    private Long userId;

    public ApplicationDsUpdateUserParam() {
    }

    public ApplicationDsUpdateUserParam(Long applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
