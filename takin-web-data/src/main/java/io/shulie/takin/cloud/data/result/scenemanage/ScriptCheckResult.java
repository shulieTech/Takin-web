package io.shulie.takin.cloud.data.result.scenemanage;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/20 下午8:24
 */
@Data
public class ScriptCheckResult {

    private Boolean matchActivity = true;

    private Boolean ptTag = true;

    private String errmsg;

    public ScriptCheckResult() {

    }

    public ScriptCheckResult(Boolean matchActivity, Boolean ptTag, String errmsg) {
        this.matchActivity = matchActivity;
        this.ptTag = ptTag;
        this.errmsg = errmsg;
    }
}
