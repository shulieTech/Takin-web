package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/22 上午4:14
 */
@Data
public class ScriptUrlVO implements Serializable {

    private static final long serialVersionUID = 2155178590508223791L;

    private String name;

    private Boolean enable;

    private String path;

    public ScriptUrlVO() {

    }

    public ScriptUrlVO(String name, Boolean enable, String path) {
        this.name = name;
        this.enable = enable;
        this.path = path;
    }
}
