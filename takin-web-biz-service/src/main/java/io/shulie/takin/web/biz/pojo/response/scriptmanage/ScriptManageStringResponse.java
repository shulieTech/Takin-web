package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageStringResponse implements Serializable {
    private static final long serialVersionUID = -262737931928766813L;

    private String content;

    public ScriptManageStringResponse(String content) {
        this.content = content;
    }
}
