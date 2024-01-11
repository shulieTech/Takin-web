package io.shulie.takin.web.biz.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class ParseScriptNodeVO implements Serializable {

    private Integer javaRequestCount = 0;

    private Set<String> csvFileSet = new HashSet<>();

}
