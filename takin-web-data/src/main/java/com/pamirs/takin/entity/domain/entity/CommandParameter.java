package com.pamirs.takin.entity.domain.entity;

/**
 * Created by Windows User on 2019/5/10.
 */
public class CommandParameter extends BaseEntity {
    private String name;

    private String desc;

    private Boolean required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "{" +
            "name:'" + name + '\'' +
            ", desc:'" + desc + '\'' +
            ", required:'" + required + '\'' +
            '}';
    }
}
