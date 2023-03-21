package io.shulie.takin.cloud.common.script.jmeter;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScriptJsonProcessor implements Serializable {

    private String referenceNames;

    private String jsonPathExprs;

    private String matchNumbers;
}
