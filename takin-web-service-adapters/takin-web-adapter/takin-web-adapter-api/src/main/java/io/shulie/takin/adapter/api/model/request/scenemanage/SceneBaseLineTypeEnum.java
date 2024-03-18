package io.shulie.takin.adapter.api.model.request.scenemanage;

/**
 * @author zhangz
 * Created on 2024/3/11 18:11
 * Email: zz052831@163.com
 */


public enum SceneBaseLineTypeEnum {
    NONE(0), TIME(1), REPORT(2);
    private int type;

    SceneBaseLineTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
