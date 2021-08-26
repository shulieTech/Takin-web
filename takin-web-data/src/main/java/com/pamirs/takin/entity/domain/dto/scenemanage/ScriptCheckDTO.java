package com.pamirs.takin.entity.domain.dto.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/20 下午8:24
 */
@Data
public class ScriptCheckDTO implements Serializable {

    private static final long serialVersionUID = 9156507579287192742L;

    private Boolean matchActivity = true;

    private Boolean ptTag = true;

    private String errmsg;

    /**
     * 是否只存在http请求，脚本校验使用
     */
    private Boolean isHttp;

    public ScriptCheckDTO() {

    }

    public ScriptCheckDTO(Boolean matchActivity, Boolean ptTag, String errmsg) {
        this.matchActivity = matchActivity;
        this.ptTag = ptTag;
        this.errmsg = errmsg;
    }
}
