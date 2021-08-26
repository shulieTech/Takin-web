package com.pamirs.takin.entity.domain.vo;

import lombok.Data;

@Data
public class JarVersionVo {

    private String pluginName;
    private String jarName;
    private String jarType;
    private boolean active;
    private Boolean hidden;
}
