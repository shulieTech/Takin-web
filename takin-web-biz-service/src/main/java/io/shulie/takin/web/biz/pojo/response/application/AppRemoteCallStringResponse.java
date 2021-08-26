package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallStringResponse {
    private String content;
    public AppRemoteCallStringResponse(String content) {
        this.content = content;
    }
}
