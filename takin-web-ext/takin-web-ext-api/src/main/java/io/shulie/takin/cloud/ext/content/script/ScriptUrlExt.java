package io.shulie.takin.cloud.ext.content.script;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/22 上午4:14
 */
@Data
public class ScriptUrlExt {

    private String name;

    private Boolean enable;

    private String path;

    public ScriptUrlExt() {

    }

    public ScriptUrlExt(String name, Boolean enable, String path) {
        this.name = name;
        this.enable = enable;
        this.path = path;
    }
}
