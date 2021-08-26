package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/25 7:58 下午
 */
@Data
public class ApplicationWhiteListUpdateParam {
    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 用户id
     */
    private Long userId;

    public ApplicationWhiteListUpdateParam() {
    }

    public ApplicationWhiteListUpdateParam(String applicationId, Long userId) {
        this.applicationId = applicationId;
        this.userId = userId;
    }
}
