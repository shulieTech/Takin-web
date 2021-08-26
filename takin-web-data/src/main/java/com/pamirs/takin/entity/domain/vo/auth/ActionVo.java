package com.pamirs.takin.entity.domain.vo.auth;

/**
 * @author fanxx
 * @date 2020/9/4 下午7:33
 */
public class ActionVo {
    private String name;

    public ActionVo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
