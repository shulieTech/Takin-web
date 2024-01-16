package io.shulie.takin.web.biz.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class ParseScriptNodeVO implements Serializable {

    private Set<String> javaRequestClass = new HashSet<>();

    private Set<String> jdbcRequestClass = new HashSet<>();

    private Set<String> pluginRequestClass = new HashSet<>();

    private Set<String> csvFileSet = new HashSet<>();
}
