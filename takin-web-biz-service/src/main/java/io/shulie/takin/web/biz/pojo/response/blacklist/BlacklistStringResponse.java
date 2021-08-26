package io.shulie.takin.web.biz.pojo.response.blacklist;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/28 9:43 上午
 */
@Data
public class BlacklistStringResponse {
    private String content;

    public BlacklistStringResponse(String content) {
        this.content = content;
    }
}
