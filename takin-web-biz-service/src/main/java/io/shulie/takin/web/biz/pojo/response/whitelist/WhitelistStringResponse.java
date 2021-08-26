package io.shulie.takin.web.biz.pojo.response.whitelist;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 5:56 下午
 */
@Data
public class WhitelistStringResponse {
    private String content;

    public WhitelistStringResponse(String content) {
        this.content = content;
    }

}
