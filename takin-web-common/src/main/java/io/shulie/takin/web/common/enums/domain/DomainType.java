package io.shulie.takin.web.common.enums.domain;

/**
 * @Author: fanxx
 * @Date: 2021/12/8 9:54 上午
 * @Description:
 */
public enum DomainType {
    DEFAULT(0),
    USER_DEFINE(1);

    private int type;

    DomainType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
